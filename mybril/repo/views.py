from eulfedora.server import Repository
from mybril.repo.models import FileObject
from django.shortcuts import render_to_response
from django.template import RequestContext
from eulfedora.views import raw_datastream

def display(request, pid):
    repo = Repository()
    obj = repo.get_object(pid, type=FileObject)
    return render_to_response('repo/display.html', {'obj': obj})
    
def file(request, pid):
    dsid = FileObject.file.id
    repo = Repository()
    obj = repo.get_object(pid, type=FileObject)
    extra_headers = {
        'Content-Disposition': "attachment; filename=%s" % obj.file.label,
    }
    return raw_datastream(request, pid, dsid, type=FileObject, headers=extra_headers)
