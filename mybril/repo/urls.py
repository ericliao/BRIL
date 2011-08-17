from django.conf.urls.defaults import *

urlpatterns = patterns('repo.views',
    url(r'^objects/(?P<pid>[^/]+)/$', 'display', name='display'),
    url(r'^objects/(?P<pid>[^/]+)/view/$', 'view_object', name='view'),
    url(r'^objects/(?P<pid>[^/]+)/file/$', 'file', name='download'),
)
