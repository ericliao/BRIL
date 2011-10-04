# Modified by Eric Liao
# Contact: the.eric.liao@gmail.com

from django.conf.urls.defaults import *

urlpatterns = patterns('repo.views',

    # aggregation related views
    url(r'^aggregations/(?P<aggId>[^/]+)/rdfxml/$', 'rdfxml'),
    
    # experiment related views
    url(r'^experiments/(?P<expId>[^/]+)/$', 'display_experiment'),
    url(r'^experiments/(?P<expId>[^/]+)/relationships/$', 'exp_relationships'),
    url(r'^experiments/(?P<expId>[^/]+)/details/$', 'display_experiment'),
    url(r'^experiments/(?P<expId>[^/]+)/ORE/$', 'generate_experiment_ORE'),
    url(r'^experiments/(?P<expId>[^/]+)/savePNG/$', 'save_PNG'),
    url(r'^experiments/(?P<expId>[^/]+)/getPNG/$', 'get_PNG'),
        
    # object related views
    url(r'^objects/(?P<pid>[^/]+)/$', 'get_object_relationships'),
    url(r'^objects/(?P<pid>[^/]+)/details/$', 'display'),
    url(r'^objects/(?P<pid>[^/]+)/view/$', 'view_object'),
    url(r'^objects/(?P<pid>[^/]+)/file/$', 'file', name='download'),
)
