# Modified by Eric Liao
# Contact: the.eric.liao@gmail.com

from django.db import models
from eulfedora.models import DigitalObject, FileDatastream, Datastream

class FileObject(DigitalObject):
    FILE_CONTENT_MODEL = 'info:fedora/genrepo:File-1.0'
    CONTENT_MODELS = [ FILE_CONTENT_MODEL ]
    file = FileDatastream("MYDS", "Binary datastream", defaults={'versionable': True,})
    brilmeta = Datastream("BRILMETA", "BRIL Metadata", defaults={'versionable': True,})
