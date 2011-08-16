from django.conf.urls.defaults import *

urlpatterns = patterns('repo.views',
    url(r'^objects/(?P<pid>[^/]+)/$', 'display', name='display'),
    url(r'^objects/(?P<pid>[^/]+)/file/$', 'file', name='download'),
)
