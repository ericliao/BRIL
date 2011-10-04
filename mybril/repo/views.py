# Modified by Eric Liao
# Contact: the.eric.liao@gmail.com

import os, string, simplejson, uuid
from foresite import *
from rdflib import URIRef, Namespace
from eulfedora.server import Repository
from mybril.repo.models import FileObject, AggregationObject
from django.shortcuts import render_to_response
from django.http import HttpResponse
from django.template import RequestContext
from eulfedora.views import raw_datastream
from datetime import datetime
from base64 import decodestring
from PIL import Image, ImageDraw

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

def rdfxml(request, aggId):
    dsid = AggregationObject.rdfxml.id
    repo = Repository()
    obj = repo.get_object(aggId, type=AggregationObject)
    filename = os.path.basename(obj.dc.content.title)
    extra_headers = {
        'Content-Disposition': "attachment; filename=%s" % filename,
    }
    return raw_datastream(request, aggId, dsid, type=AggregationObject, headers=extra_headers)

def get_object_relationships(request, pid):
        
    # Find the experiment this object belongs to
    repo = Repository()
    experiment = repo.risearch.get_objects("info:fedora/" + pid, "info:fedora/fedora-system:def/relations-external#isPartOf")        
    expId = experiment.next();
    exp_obj = repo.get_object(expId)
    exp_title = exp_obj.dc.content.title
    exp_description = exp_obj.dc.content.description
    exp_created = exp_obj.info.created
    exp_modified = exp_obj.info.modified
    
    expId = string.replace(expId, 'info:fedora/', '')
              
    related_objects = []
    relationships = []           
    aggregated_objects = {}
    
    obj = repo.get_object(pid, type=FileObject)                           
    
    # Search for relationships to the object and add each related object as an Aggregated Resource
    if (string.find(obj.pid, "process") != -1):
      for related_pid in repo.risearch.get_objects("info:fedora/"+ obj.pid, "http://purl.org/net/opmv/ns#used"):
        related_objects.append(repo.get_object(related_pid))
        relationships.append(["used", string.replace(related_pid, 'info:fedora/', '')])

    if (string.find(obj.pid, "process") == -1):
      for related_pid in repo.risearch.get_objects("info:fedora/"+ obj.pid, "http://purl.org/net/opmv/ns#wasGeneratedBy"):
        related_objects.append(repo.get_object(related_pid))  
        relationships.append(["wasGeneratedBy", string.replace(related_pid, 'info:fedora/', '')])
    
      for related_pid in repo.risearch.get_objects("info:fedora/"+ obj.pid, "http://purl.org/net/opmv/ns#wasDerivedFrom"):
        related_objects.append(repo.get_object(related_pid))
        relationships.append(["wasDerivedFrom", string.replace(related_pid, 'info:fedora/', '')])
      
      for related_pid in repo.risearch.get_objects("info:fedora/"+ obj.pid, "info:fedora/fedora-system:def/relations-external#isMemberOf"):
        related_objects.append(repo.get_object(related_pid))
        relationships.append(["isMemberOf", string.replace(related_pid, 'info:fedora/', '')])    
  
    for related_obj in related_objects:
        aggregated_objects[related_obj.dc.content.identifier] = related_obj.dc.content.title
    
    used_relationships = []
    wasGeneratedBy_relationships = []
    wasDerivedFrom_relationships = []
    wasTriggeredBy_relationships = []
    isMemberOf_relationships = []
                
    edge_from = []
    edge_to = []
    for r in relationships: 
        if (string.find(r[0], "used") != -1):               
          used_relationships.append(r[1])
        elif (string.find(r[0], "wasGeneratedBy") != -1):
          wasGeneratedBy_relationships.append(r[1])
        elif (string.find(r[0], "wasDerivedFrom") != -1):
          wasDerivedFrom_relationships.append(r[1])
        elif (string.find(r[0], "wasTriggeredBy") != -1):
          wasTriggeredBy_relationships.append(r[1])
        elif (string.find(r[0], "isMemberOf") != -1):
          isMemberOf_relationships.append(r[1])
        
    # Generate related object links for display
    # TODO: convert this to a multiselect box
    related_objects_html = ""    
    if len(used_relationships) > 0:
        related_objects_html += "<h3>used</h3><table>"
        for r in used_relationships:
            related_objects_html += "<tr><td><a href='http://localhost:8000/repo/objects/" + r + "'>" + aggregated_objects[r] + "</a></td></tr>"
        related_objects_html += "</table>"
        
    if len(wasGeneratedBy_relationships) > 0:    
        related_objects_html += "<h3>wasGeneratedBy</h3><table>"
        for r in wasGeneratedBy_relationships:
            related_objects_html += "<tr><td><a href='http://localhost:8000/repo/objects/" + r + "'>" + aggregated_objects[r] + "</a></td></tr>"
        related_objects_html += "</table>"

    if len(wasDerivedFrom_relationships) > 0:
        related_objects_html += "<h3>wasDerivedFrom</h3><table>"
        for r in wasDerivedFrom_relationships:
            related_objects_html += "<tr><td><a href='http://localhost:8000/repo/objects/" + r + "'>" + aggregated_objects[r] + "</a></td></tr>"
        related_objects_html += "</table>"
        
    if len(wasTriggeredBy_relationships) > 0:    
        related_objects_html += "<h3>wasTriggeredBy</h3><table>"
        for r in wasTriggeredBy_relationships:
            related_objects_html += "<tr><td><a href='http://localhost:8000/repo/objects/" + r + "'>" + aggregated_objects[r] + "</a></td></tr>"
        related_objects_html += "</table>"    

    if len(isMemberOf_relationships) > 0:    
        related_objects_html += "<h3>isMemberOf</h3><table>"
        for r in isMemberOf_relationships:
            related_objects_html += "<tr><td><a href='http://localhost:8000/repo/objects/" + r + "'>" + aggregated_objects[r] + "</a></td></tr>"
        related_objects_html += "</table>"    
    
    experiment_html = "<h3>isPartOf</h3><table><tr><td><a href='http://localhost:8000/repo/experiments/" + expId + "'>" + exp_title + ":" + exp_description + "</a></td></tr></table>"   
    related_html = related_objects_html + experiment_html;
    return render_to_response('repo/display.html', {'obj': obj, 'related': related_html})

def display_experiment(request, expId):
    repo = Repository()
    exp_obj = repo.get_object(expId, type=FileObject)
    exp_pids = repo.risearch.get_subjects("info:fedora/fedora-system:def/relations-external#isPartOf", "info:fedora/" + expId)
    related_objects = [] 
    processes = []
    objects = []
    
    # Generate related object links for display
    for obj_pid in exp_pids:    
        related_objects.append(repo.get_object(obj_pid))
            
    for obj in related_objects:
        if (string.find(obj.pid, "process") != -1):
            processes.append(obj)
    for obj in related_objects:
        if (string.find(obj.pid, "process") == -1):
            objects.append(obj)
      
    related_objects_html = "<h3>Processes</h3><table class='scrollTable' width='100%'><tbody class='scrollContent'>"
    for o in processes:
        related_objects_html += "<tr><td><a href='http://localhost:8000/repo/objects/" + o.dc.content.identifier + "'>" + o.dc.content.title + "</a></td></tr>"
    
    related_objects_html += "</tbody></table><br/><h3>Artefacts</h3><table class='scrollTable' width='100%'><tbody class='scrollContent'>"
    for o in objects:
        related_objects_html += "<tr><td><a href='http://localhost:8000/repo/objects/" + o.dc.content.identifier + "'>" + o.dc.content.title + "</a></td></tr>"
    related_objects_html += "</tbody></table>"    
    
    # Add link for downloading aggregation RDF/XML
    aggregations = []
    agg_pids = repo.risearch.get_subjects("http://purl.org/net/opmv/ns#used", "info:fedora/" + expId)
    for agg_pid in agg_pids:
        agg_o = repo.get_object(pid = agg_pid, type = AggregationObject)
        aggregations.append(agg_o)
    
    if len(aggregations) == 0: 
        agg_url = "<p style='float:right;'>No RDF/XML available</p>"
    else:
        agg_url = "<p style='float:right;'><a href='http://localhost:8000/repo/aggregations/" + agg_o.dc.content.identifier + "/rdfxml'>RDF/XML</a></p>"
    
    return render_to_response('repo/display.html', {'obj': exp_obj, 'agg': agg_url, 'rdfa': agg_o.rdfa.content, 'related': related_objects_html})

def save_PNG(request, expId):
    if request.is_ajax():
      if request.method == 'POST':          
            
        # save canvas data to PNG file
        filename = expId + ".png"
        data = request.POST['data']
        imagestr = data[data.find(',') + 1:]                
        fh = open('/home/eric/workspace/mybril/media/' + filename, 'wb')                
        fh.write(decodestring(imagestr))
        fh.close()        
        return HttpResponse(status=240)
    else:
        return HttpResponse(status=400)

def get_PNG(request, expId):

    filename = expId + ".png"
    im = Image.open('/home/eric/workspace/mybril/media/' + filename)
    
    # return PNG file for download
    response = HttpResponse(mimetype='image/png')
    im.save(response, 'PNG')
    response['Content-Disposition'] = 'attachment; filename='+ filename
    return response

def generate_experiment_ORE(request, expId):
    
    # Add OPMV namespace
    utils.namespaces['opmv'] = Namespace('http://purl.org/net/opmv/ns#')
    utils.namespaceSearchOrder.append('opmv')
    
    # Get details of the experiment
    repo = Repository()
    exp_obj = repo.get_object("info:fedora/" + expId)
    exp_title = exp_obj.dc.content.title
    exp_description = exp_obj.dc.content.description
    exp_created = exp_obj.info.created
    exp_modified = exp_obj.info.modified
    
    # Create aggregation using foresite OAI-ORE library
    a = Aggregation("http://bril.cerch.kcl.ac.uk/aggregation/" + expId)
    a.title = "OAI-ORE Aggregation of Experiment: " + exp_title
    a._dcterms.abstract = "OAI-ORE Aggregation of: " + exp_description
    a._dcterms.created = datetime.now().isoformat(' ')
    creator = Agent("http://bril.cerch.kcl.ac.uk")
    creator.name = "BRIL Repository"
    a.add_agent(creator, "creator")
    
    # Add experiment to aggregation    
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
    aggregated_objects = {}
    
    for obj_pid in exp_pids:
        o = repo.get_object(pid = obj_pid)
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
    relationships = []
    for obj in objects:
        for o in repo.risearch.get_objects("info:fedora/"+ obj.pid, "http://purl.org/net/opmv/ns#wasGeneratedBy"):
            relationships.append([obj.pid, string.replace(o, 'info:fedora/', '')])
    
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
        e_from._opmv.wasGeneratedBy = e_to
          
    # Add 'wasDerivedFrom' relationships
    relationships = []
    for obj in objects:
        for o in repo.risearch.get_objects("info:fedora/"+ obj.pid, "http://purl.org/net/opmv/ns#wasDerivedFrom"):
            relationships.append([obj.pid, string.replace(o, 'info:fedora/', '')])
    
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
        e_from._opmv.wasDerivedFrom = e_to
    
    relationships = []
    for obj in objects:
        for o in repo.risearch.get_objects("info:fedora/"+ obj.pid, "info:fedora/fedora-system:def/relations-external#isMemberOf"):
            relationships.append([obj.pid, string.replace(o, 'info:fedora/', '')])
        
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
    
    # Add resource map and generate RDFa + RDF/XML
    rdfa = RdfLibSerializer('rdfa')
    rdfxml = RdfLibSerializer('xml')
    rem_rdfa = ResourceMap("http://bril.cerch.kcl.ac.uk/rem/rdf/" + expId)
    rem_rdfa.set_aggregation(a)
    rem_rdfa.register_serialization(rdfa)
    rdfa_doc = rem_rdfa.get_serialization()   
    
    rem_rdfxml = ResourceMap("http://bril.cerch.kcl.ac.uk/rem/rdf/" + expId)
    rem_rdfxml.set_aggregation(a)
    rem_rdfxml.register_serialization(rdfxml)
    rdfxml_doc = rem_rdfxml.get_serialization()
    
    # Look for an existing aggregation for this experiment
    aggregations = []
    agg_pids = repo.risearch.get_subjects("http://purl.org/net/opmv/ns#used", "info:fedora/" + expId)
    for agg_pid in agg_pids:
        o = repo.get_object(pid = agg_pid, type = AggregationObject)
        aggregations.append(o)
    
    if len(aggregations) == 0: 
        # There is no existing aggregation for this experiment, create Aggregation fedora object in repository
        # + add rdfa + rdfxml as datastreams + add relationship to experiment
        agg_obj = repo.get_object(type = AggregationObject)
        agg_obj.dc.content.title = "OAI-ORE Aggregation of Experiment: " + exp_title
        agg_obj.dc.content.description = "OAI-ORE Aggregation of: " + exp_description
        agg_obj.rdfa.content = rdfa_doc.data
        agg_obj.rdfxml.content = rdfxml_doc.data
        agg_obj.save()
        agg_obj.add_relationship("http://purl.org/net/opmv/ns#used", exp_obj)
        return HttpResponse(content="New OAI-ORE Aggregation object generated!")
    else:
        # There is an existing aggregation, just modify the rdfa + rdfxml datastreams
        aggregations[0].rdfa.content = rdfa_doc.data
        aggregations[0].rdfxml.content = rdfxml_doc.data
        aggregations[0].save()
        return HttpResponse(content="Modified existing OAI-ORE Aggregation object!")
      
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
        o = repo.get_object(pid = obj_pid)
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
        edges.append(edge)        
   
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
        edges.append(edge)        

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
        edges.append(edge)
    
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
        edges.append(edge)
    
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
        edges.append(edge)

    # TODO: scan nodes for unique formats

    # JSONify and return
    json_output = simplejson.JSONEncoder().encode(dict(nodes=nodes, edges=edges))
    return HttpResponse(json_output, mimetype="application/json")  
