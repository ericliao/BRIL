/* Copyright (c) 2006-2008 MetaCarta, Inc., published under the Clear BSD
 * license.  See http://svn.openlayers.org/trunk/openlayers/license.txt for the
 * full text of the license. */

/**
 * CanvasBarrier is a helper class, to execute an operation
 * on a canvas' pixel data in several web workers. The canvas
 * is cut into rows and each web worker independently processes
 * its part. When all web worker are finished, the method
 * 'callbackDone' is triggered.
 * 
 * @param {Object} numberOfWorkers - How many web worker should be used?
 * @param {Object} canvasContext - The canvas' drawing context.
 * @param {Object} workerScript - Path to the script which contains the web worker code.
 * @param {Object} callbackDone - Triggered when all web workers are finished
 * @param {Object} callbackStatus - Optional, triggered on a status update.
 * @param {Object} parameters - Optional, additional parameters that are passed to each worker.
 */
var CanvasBarrier = function(numberOfWorkers, canvasContext, workerScript, callbackDone, 
                                                                callbackStatus, parameters) {
    
    this.numberOfWorkers = numberOfWorkers;
    this.callbackDone = callbackDone;
    this.callbackStatus = callbackStatus;
    this.canvasContext = canvasContext;
    this.parameters = parameters;
    
    this.runningWorkers = 0;
    this.error = null;
    
    /**
     * Start the web workers.
     */
    this.start = function() {
        var canvasWidth = this.canvasContext.canvas.width;
        var canvasHeight = this.canvasContext.canvas.height;
        
        this.runningWorkers = this.numberOfWorkers;
        this.progress = [];
        this.lastProgress = 0;
        
        // create a web worker for each row
        for (var i = 0; i < this.numberOfWorkers; i++) {
            this.progress.push(0);
            
            // get the position of this row
            var x = 0;
            var y = this.getRowPositionY(i, canvasHeight);
            var width =  canvasWidth;
            var height =  this.getRowPositionY(i+1, canvasHeight) - y;
            
            // get the pixel data for this row
            var imageData = this.canvasContext.getImageData(x, y, width, height);
            
            // now we could use the keyword 'let' for not having to do this cascaded closures:
            // https://developer.mozilla.org/en/Core_JavaScript_1.5_Guide/Working_with_Closures#Creating_closures_in_loops.3a_A_common_mistake
            var onMessage = this.getOnMessageCallback(i, this.canvasContext, x, y, this);
            var onError = this.getOnErrorCallback(this);
            
            // start a web worker
            var worker = new Worker(workerScript);
            worker.onmessage = onMessage;
            worker.onerror = onError;
            
            var task = {
                parameters: this.parameters,
                imageData: imageData
            }
            
            worker.postMessage(task);   
        }    
    };
    
    /**
     * Returns the y-position for the given row. 
     * 
     * @param {Object} i - Row index
     * @param {Object} height - The canvas' height
     * @return {Integer} - Y-Position
     */
    this.getRowPositionY = function(i,height) {
        return Math.floor(height / this.numberOfWorkers * i);    
    };
    
    /**
     * Returns a callback function that is used as
     * onmessage handler for the web worker.
     * 
     * @param {Object} index - Row index
     * @param {Object} canvasContext - Canvas context
     * @param {Object} x - X-Position of the row 
     * @param {Object} y - Y-Position of the row
     * @param {Object} barrier
     */
    this.getOnMessageCallback = function(index, canvasContext, x, y, barrier) {
        var context =  {
                    index: index,
                    canvasContext: canvasContext,
                    x: x,
                    y: y,
                    barrier: barrier    
        };  
        
        return function(event) {
            if (event.data.status === "progress" && context.barrier.callbackStatus) {
                context.barrier.reportProgress(context.index, event.data.progress);
            } else if (event.data.status === "done") {
                var imageData = event.data.imageData;
                // directly write the row on the canvas
                context.canvasContext.putImageData(imageData, context.x, context.y);  
                context.barrier.checkRunningWorkers();     
            }                
        };
    };
    
    /**
     * Returns a callback function that is used as
     * onerror handler for the web worker.
     * 
     * @param {Object} barrier
     */
    this.getOnErrorCallback = function(barrier) {
        return function(error) {
            barrier.error = error;
            barrier.checkRunningWorkers();
        };
    };
    
    /**
     * Called from the onmessage and onerror callback. When
     * all web workers are finished, 'callbackDone' is triggered.
     */
    this.checkRunningWorkers = function() {
        this.runningWorkers--;
        
        if (this.runningWorkers === 0) {
            // all workers are finished
            this.callbackDone({
                    canvas: this.canvas,
                    error: this.error
                });
        }   
    };
    
    /**
     * Each web worker individually reports its progress. This method
     * calculates the overall progress and calls 'callbackStatus'.
     * 
     * @param {Object} index - The row index.
     * @param {Object} progress - The progress for this row.
     */
    this.reportProgress = function(index, progress) {
        // update the progress for this worker
        this.progress[index] = progress;
        
        // then calculate the overall progress of all workers
        var sum = 0;
        for (var i = 0; i < this.progress.length; i++) {
            sum += this.progress[i];
        }
        var overallProgress = Math.round(sum / (this.numberOfWorkers * 100) * 100);
        
        if (overallProgress > this.lastProgress) {
            this.lastProgress = overallProgress;
            this.callbackStatus(overallProgress); 
        }           
    };
};
