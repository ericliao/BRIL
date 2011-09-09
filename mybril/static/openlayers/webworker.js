/* Copyright (c) 2006-2008 MetaCarta, Inc., published under the Clear BSD
 * license.  See http://svn.openlayers.org/trunk/openlayers/license.txt for the
 * full text of the license. */

/**
 * This script is called as web worker from HeatMap.createAsync().
 */

onmessage = function(event) {
    var options = event.data;
    
    importScripts("heatmap.js");

    // start the coloring
    colorize(options);
    
    // send the result image data back to the main script
    var result = {
        status: "done",
        imageData: options.imageData        
    };
    postMessage(result);
    
    // important: now terminate the worker, because some browser (Chrome 6)
    // only allow a limited number of workers
    close();
};

var colorize = function(options) {
    var handlerProgress = function(event) {
        postMessage({
            status: "progress",
            progress: event.progress
        });    
    };
    
    HeatMap.colorizeIntensityMask(options.imageData, options.parameters.colorMap, handlerProgress);    
};
