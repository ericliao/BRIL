from eulfedora.server import Repository
from mybril.repo.models import FileObject
from django.shortcuts import render_to_response
from django.http import HttpResponse
from django.template import RequestContext
from eulfedora.views import raw_datastream
import os, json, string

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

def exp_relationships(request, expId):
    repo = Repository()
    exp_pids = repo.risearch.get_subjects("info:fedora/fedora-system:def/relations-external#isPartOf", "info:fedora/" + expId)
    node_id = 0
    nodes = []
    edges = []
    objects = []
    processes = []
    controllers = []
    relationship = []
    for obj_pid in exp_pids:
        o = repo.get_object(pid = obj_pid);
        objects.append(o)
    for obj in objects:
        if (string.find(obj.pid, "process") != -1):
            processes.append(obj)
            objects.remove(obj)
    
    for process in processes:
        for o in repo.risearch.get_objects("info:fedora/"+ process.pid, "http://purl.org/net/opmv/ns#wasControlledBy"):
            controllers.append([process.pid, o])
    
    # generate controller nodes
    for controller in controllers:
        controller_node = dict(id=node_id, data=dict(label=controller[1], shape='hexagon', pid='null'))
        nodes.append(controller_node)
        node_id += 1

    # generate process nodes
    for process in processes:
        process_node = dict(id=node_id, data=dict(label=process.dc.content.title, shape='square', pid=process.dc.content.identifier))
        nodes.append(process_node)
        node_id += 1
   
    # generate process -> controller edges
    edge_from = []
    edge_to = []
    for controller in controllers:
        # search for matching pid in nodes
        for node in nodes:
            if ((node.get('data', {}).get('pid', {})) == controller[0]): # controller pid matches processes pid
                edge_from.append(node.get('id', {}))
            if ((node.get('data', {}).get('label', {})) == controller[1]): # controller label matches node label
                edge_to.append(node.get('id', {}))
    
    for e_from, e_to in zip(edge_from, edge_to):           
        edge = dict(_from=e_from, _to=e_to, directed='true', data=dict(color='#DA70D6', text='wasControlledBy'))
        edges.append(edge);        
    
    # generate object nodes
    for obj in objects:
        object_node = dict(id=node_id, data=dict(label=obj.dc.content.title, shape='circle', pid=obj.dc.content.identifier))
        nodes.append(object_node)
        node_id += 1
             
    # generate 'used 'edges
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
        edge = dict(_from=e_from, _to=e_to, directed='true', data=dict(color='#6A4A3C', text='used'))
        edges.append(edge);

    # generate 'isMemberOf 'edges
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
        edge = dict(_from=e_from, _to=e_to, directed='true', data=dict(color='#00A0B0', text='isMemberOf'))
        edges.append(edge);    
    
    # generate 'wasDerivedFrom' edges
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
        edge = dict(_from=e_from, _to=e_to, directed='true', data=dict(color='#EB6841', text='wasDerivedFrom'))
        edges.append(edge);      
    
    # generate 'wasGeneratedBy' edges
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
        edge = dict(_from=e_from, _to=e_to, directed='true', data=dict(color='#7DBE3C', text='wasGeneratedBy'))
        edges.append(edge);

    # JSONify and return
    json_output = json.JSONEncoder().encode(dict(nodes=nodes, edges=edges))
    return HttpResponse(json_output, mimetype="application/json")
    
    
    
    
    
    
