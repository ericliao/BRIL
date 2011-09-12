# Modified by Eric Liao
# Contact: the.eric.liao@gmail.com

import os, string, simplejson
from foresite import *
from rdflib import URIRef, Namespace
from eulfedora.server import Repository
from mybril.repo.models import FileObject
from django.shortcuts import render_to_response
from django.http import HttpResponse
from django.template import RequestContext
from eulfedora.views import raw_datastream
from datetime import datetime

def display(request, pid):
    repo = Repository()
    obj = repo.get_object(pid, type=FileObject)
    return render_to_response('repo/display.html', {'obj': obj})

def view_object(request, pid):
    repo = Repository()
    obj = repo.get_object(pid, type=FileObject)
    return render_to_response('repo/viewer.html', {'obj': obj})
    
def file(request, pid):
    dsid = FileObject.file.id
    repo = Repository()
    obj = repo.get_object(pid, type=FileObject)
    filename = os.path.basename(obj.dc.content.title)
    extra_headers = {
        'Content-Disposition': "attachment; filename=%s" % filename,
    }
    return raw_datastream(request, pid, dsid, type=FileObject, headers=extra_headers)

def export_ORE(request, expId):
    
    # add OPMV namespace
    utils.namespaces['opmv'] = Namespace('http://purl.org/net/opmv/ns#')
    utils.namespaceSearchOrder.append('opmv')
    
    # Get details of the experiment
    repo = Repository()
    exp_obj = repo.get_object("info:fedora/" + expId)
    exp_title = exp_obj.dc.content.title
    exp_description = exp_obj.dc.content.description
    exp_created = exp_obj.info.created
    exp_modified = exp_obj.info.modified
    
    # create aggregation using foresite OAI-ORE library
    a = Aggregation("http://bril.cerch.kcl.ac.uk/aggregation/" + expId)
    a.title = "OAI-ORE Aggregation of Experiment: " + exp_title
    a._dcterms.abstract = "OAI-ORE Aggregation of: " + exp_description
    # TODO: get current time
    a._dcterms.created = datetime.now().isoformat(' ')
    creator = Agent("http://bril.cerch.kcl.ac.uk")
    creator.name = "BRIL Repository"
    a.add_agent(creator, "creator")
    
    # add experiment to aggregation    
    exp = AggregatedResource("http://bril.cerch.kcl.ac.uk/" + expId)
    exp.title = exp_title
    exp._dcterms.abstract = exp_description
    exp._dcterms.created = exp_created
    exp._dcterms.modified = exp_modified
    exp_agent = Agent("http://bril.cerch.kcl.ac.uk/agents/stella")
    exp_agent.name = "Stella Fabiane"
    exp.add_agent(exp_agent, "creator")
    a.add_resource(exp)
    
    # Get objects in the experiment
    exp_pids = repo.risearch.get_subjects("info:fedora/fedora-system:def/relations-external#isPartOf", "info:fedora/" + expId)    
    objects_cache = []
    objects = []
    processes = []
    relationship = []           
    aggregated_objects = {}
    
    for obj_pid in exp_pids:
        o = repo.get_object(pid = obj_pid);
        objects_cache.append(o)
        
    for obj in objects_cache:
        if (string.find(obj.pid, "process") != -1):
            processes.append(obj)
    for obj in objects_cache:
        if (string.find(obj.pid, "process") == -1):
            objects.append(obj)
    
    # Generate the resources to be aggregated
    for obj in objects:
        artefact = AggregatedResource("http://bril.cerch.kcl.ac.uk/" + obj.dc.content.identifier)
        artefact.title = obj.dc.content.title
        artefact._dcterms.abstract = obj.dc.content.description
        artefact._dcterms.created = obj.info.created
        artefact._dcterms.modified = obj.info.modified
        aggregated_objects[obj.dc.content.identifier] = artefact
    
    for proc in processes:
        process = AggregatedResource("http://bril.cerch.kcl.ac.uk/" + proc.dc.content.identifier)
        process.title = proc.dc.content.title
        process._dcterms.abstract = proc.dc.content.description
        process._dcterms.created = proc.info.created
        process._dcterms.modified = proc.info.modified
        aggregated_objects[proc.dc.content.identifier] = process
    
      
    # Add 'used' relationships
    relationships = []
    for process in processes:
        for o in repo.risearch.get_objects("info:fedora/"+ process.pid, "http://purl.org/net/opmv/ns#used"):
            relationships.append([process.pid, string.replace(o, 'info:fedora/', '')])
    
    edge_from = []
    edge_to = []
    for r in relationships:
        # Search for matching pid in objects
        for pid, agg_object in aggregated_objects.iteritems():
        
            if (pid == r[0]):
                edge_from.append(agg_object)
            if (pid == r[1]):
                edge_to.append(agg_object)
    
    for e_from, e_to in zip(edge_from, edge_to):           
        e_from._opmv.used = e_to
    
    
    # Add 'wasGeneratedBy' relationships
    relationship = []
    for obj in objects:
        for o in repo.risearch.get_objects("info:fedora/"+ obj.pid, "http://purl.org/net/opmv/ns#wasGeneratedBy"):
            relationship.append([obj.pid, string.replace(o, 'info:fedora/', '')])
    
    edge_from = []
    edge_to = []
    for r in relationships:
        # Search for matching pid in objects
        for pid, agg_object in aggregated_objects.iteritems():
        
            if (pid == r[0]):
                edge_from.append(agg_object)
            if (pid == r[1]):
                edge_to.append(agg_object)
    
    for e_from, e_to in zip(edge_from, edge_to):           
        e_to._opmv.wasGeneratedBy = e_from
          
    # Add 'wasDerivedFrom' relationships
    relationship = []
    for obj in objects:
        for o in repo.risearch.get_objects("info:fedora/"+ obj.pid, "http://purl.org/net/opmv/ns#wasDerivedFrom"):
            relationship.append([obj.pid, string.replace(o, 'info:fedora/', '')])
    
    edge_from = []
    edge_to = []
    for r in relationships:
        # Search for matching pid in objects
        for pid, agg_object in aggregated_objects.iteritems():
        
            if (pid == r[0]):
                edge_from.append(agg_object)
            if (pid == r[1]):
                edge_to.append(agg_object)
    
    for e_from, e_to in zip(edge_from, edge_to):           
        e_to._opmv.wasDerivedFrom = e_from
    
    # TODO: convert 'isMemberOf' relationships to nested aggregation?
    relationship = []
    for obj in objects:
        for o in repo.risearch.get_objects("info:fedora/"+ obj.pid, "info:fedora/fedora-system:def/relations-external#isMemberOf"):
            relationship.append([obj.pid, string.replace(o, 'info:fedora/', '')])
        
    edge_from = []
    edge_to = []
    for r in relationships:
        # Search for matching pid in objects
        for pid, agg_object in aggregated_objects.iteritems():
        
            if (pid == r[0]):
                edge_from.append(agg_object)
            if (pid == r[1]):
                edge_to.append(agg_object)
    
    for e_from, e_to in zip(edge_from, edge_to):           
        e_from._opmv.isMemberOf = e_to
            
    for key, agg_object in aggregated_objects.iteritems():
        a.add_resource(agg_object)
    
    # Add modified time for aggregation
    a._dcterms.modified = datetime.now().isoformat(' ')
    
    # Add resource map and return XML
    rdfxml = RdfLibSerializer("xml")
    rem = ResourceMap("http://bril.cerch.kcl.ac.uk/rem/rdf/" + expId)
    rem.set_aggregation(a)
    remdoc = rem.register_serialization(rdfxml)
    remdoc = rem.get_serialization()    
    response = HttpResponse(remdoc.data, mimetype='application/xml')
    response['Content-Disposition'] = 'attachment; filename='+ expId + '-ORE.xml'
    return response
      
def exp_relationships(request, expId):
    repo = Repository()
    exp_pids = repo.risearch.get_subjects("info:fedora/fedora-system:def/relations-external#isPartOf", "info:fedora/" + expId)
    node_id = 0
    nodes = []
    edges = []
    objects_cache = []
    objects = []
    processes = []   
    relationship = []
    for obj_pid in exp_pids:
        o = repo.get_object(pid = obj_pid);
        objects_cache.append(o)
        
    for obj in objects_cache:
        if (string.find(obj.pid, "process") != -1):
            processes.append(obj)
    for obj in objects_cache:
        if (string.find(obj.pid, "process") == -1):
            objects.append(obj)
       
    # generate process nodes
    for process in processes:
        process_node = dict(id=process.dc.content.identifier, data=dict(label=process.dc.content.title, shape='square', pid=process.dc.content.identifier, format='process'))        
        nodes.append(process_node)

        controllers = []
        for o in repo.risearch.get_objects("info:fedora/"+ process.pid, "http://purl.org/net/opmv/ns#wasControlledBy"):
            controllers.append(o)
        
        # generate controller nodes and 'wasControlledBy' relationships
        for controller in controllers:
            controller_node = dict(id=node_id, data=dict(label=controller, shape='hexagon', pid='null', format='controller'))
            nodes.append(controller_node)
            node_id += 1
            edge = dict(_from=process_node.get('id', {}), _to=controller_node.get('id', {}), 
                        directed='false', data=dict(color='#DA70D6', text='wasControlledBy'))                        
            edges.append(edge)
   
    # generate 'wasTriggeredBy' relationships
    relationship = []
    for process in processes:
        for o in repo.risearch.get_objects("info:fedora/"+ process.pid, "http://purl.org/net/opmv/ns#wasTriggeredBy"):
            relationship.append([process.pid, string.replace(o, 'info:fedora/', '')])
    
    edge_from = []
    edge_to = []    
    for r in relationship:
        # search for matching pid in nodes
        for node in nodes:
            if ((node.get('data', {}).get('pid', {})) == r[0]):
                edge_from.append(node.get('id', {}))
            if ((node.get('data', {}).get('pid', {})) == r[1]):
                edge_to.append(node.get('id', {}))
    
    for e_from, e_to in zip(edge_from, edge_to):           
        edge = dict(_from=e_from, _to=e_to, directed='false', data=dict(color='#FFD800', text='wasTriggeredBy'))
        edges.append(edge);
   
    # generate object nodes
    for obj in objects:
        object_node = dict(id=obj.dc.content.identifier, data=dict(label=os.path.basename(obj.dc.content.title), shape='circle', pid=obj.dc.content.identifier, format=obj.dc.content.format))
        nodes.append(object_node)
             
    # generate 'used' relationships
    relationship = []
    for process in processes:
        for o in repo.risearch.get_objects("info:fedora/"+ process.pid, "http://purl.org/net/opmv/ns#used"):
            relationship.append([process.pid, string.replace(o, 'info:fedora/', '')])
    
    edge_from = []
    edge_to = []    
    for r in relationship:
        # search for matching pid in nodes
        for node in nodes:
            if ((node.get('data', {}).get('pid', {})) == r[0]):
                edge_from.append(node.get('id', {}))
            if ((node.get('data', {}).get('pid', {})) == r[1]):
                edge_to.append(node.get('id', {}))
    
    for e_from, e_to in zip(edge_from, edge_to):           
        edge = dict(_from=e_from, _to=e_to, directed='false', data=dict(color='#6A4A3C', text='used'))
        edges.append(edge);

    # generate 'isMemberOf' relationships
    relationship = []
    for obj in objects:
        for o in repo.risearch.get_objects("info:fedora/"+ obj.pid, "info:fedora/fedora-system:def/relations-external#isMemberOf"):
            relationship.append([obj.pid, string.replace(o, 'info:fedora/', '')])
    
    edge_from = []
    edge_to = []    
    for r in relationship:
        # search for matching pid in nodes
        for node in nodes:
            if ((node.get('data', {}).get('pid', {})) == r[0]):
                edge_from.append(node.get('id', {}))
            if ((node.get('data', {}).get('pid', {})) == r[1]):
                edge_to.append(node.get('id', {}))
    
    for e_from, e_to in zip(edge_from, edge_to):           
        edge = dict(_from=e_from, _to=e_to, directed='false', data=dict(color='#00A0B0', text='isMemberOf'))
        edges.append(edge);    
    
    # generate 'wasDerivedFrom' relationships
    relationship = []
    for obj in objects:
        for o in repo.risearch.get_objects("info:fedora/"+ obj.pid, "http://purl.org/net/opmv/ns#wasDerivedFrom"):
            relationship.append([obj.pid, string.replace(o, 'info:fedora/', '')])
    
    edge_from = []
    edge_to = []
    for r in relationship:
        # search for matching pid in nodes
        for node in nodes:
            if ((node.get('data', {}).get('pid', {})) == r[0]):
                edge_from.append(node.get('id', {}))
            if ((node.get('data', {}).get('pid', {})) == r[1]):
                edge_to.append(node.get('id', {}))
    
    for e_from, e_to in zip(edge_from, edge_to):           
        edge = dict(_from=e_from, _to=e_to, directed='false', data=dict(color='#EB6841', text='wasDerivedFrom'))
        edges.append(edge);      
    
    # generate 'wasGeneratedBy' relationships
    relationship = []
    for obj in objects:
        for o in repo.risearch.get_objects("info:fedora/"+ obj.pid, "http://purl.org/net/opmv/ns#wasGeneratedBy"):
            relationship.append([obj.pid, string.replace(o, 'info:fedora/', '')])
    
    edge_from = []
    edge_to = []
    for r in relationship:
        # search for matching pid in nodes
        for node in nodes:
            if ((node.get('data', {}).get('pid', {})) == r[0]):
                edge_from.append(node.get('id', {}))
            if ((node.get('data', {}).get('pid', {})) == r[1]):
                edge_to.append(node.get('id', {}))
    
    for e_from, e_to in zip(edge_from, edge_to):           
        edge = dict(_from=e_from, _to=e_to, directed='false', data=dict(color='#7DBE3C', text='wasGeneratedBy'))
        edges.append(edge);

    # TODO: scan nodes for unique formats

    # JSONify and return
    json_output = simplejson.JSONEncoder().encode(dict(nodes=nodes, edges=edges))
    return HttpResponse(json_output, mimetype="application/json")
