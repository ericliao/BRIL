# Modified by Eric Liao
# Contact: the.eric.liao@gmail.com

from django.conf.urls.defaults import *

urlpatterns = patterns('repo.views',

    # experiment related views
    url(r'^experiments/(?P<expId>[^/]+)/$', 'display_experiment', name='display'),
    url(r'^experiments/(?P<expId>[^/]+)/relationships/$', 'exp_relationships', name='relationships'),
    url(r'^experiments/(?P<expId>[^/]+)/ORE/$', 'get_experiment_ORE', name='ORE'),
    url(r'^experiments/(?P<expId>[^/]+)/savePNG/$', 'save_PNG', name='savePNG'),
    url(r'^experiments/(?P<expId>[^/]+)/getPNG/$', 'get_PNG', name='getPNG'),
        
    # object related views
    url(r'^objects/(?P<pid>[^/]+)/$', 'get_object_ORE', name='ORE'),
    url(r'^objects/(?P<pid>[^/]+)/details/$', 'display', name='display'),
    url(r'^objects/(?P<pid>[^/]+)/view/$', 'view_object', name='view'),
    url(r'^objects/(?P<pid>[^/]+)/file/$', 'file', name='download'),
)
