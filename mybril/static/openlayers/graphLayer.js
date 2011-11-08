/*
 * Copyright (c) 2010 Bjoern Hoehrmann <http://bjoern.hoehrmann.de/>.
 * This module is licensed under the same terms as OpenLayers itself.
 *
 *  Modified by Eric Liao 2011
 *  Contact: the.eric.liao@gmail.com
 *
 */
NodeGraph = {};

/**
 * Class: NodeGraph.Layer
 * 
 * Inherits from:
 *  - <OpenLayers.Layer>
 */
NodeGraph.Layer = OpenLayers.Class(OpenLayers.Layer, {

    /** 
     * APIProperty: isBaseLayer 
     * {Boolean} NodeGraph layer is never a base layer.  
     */
    isBaseLayer: false,
    /**
     * Constructor: NodeGraph.Layer
     * Create a NodeGraph layer.
     *
     * Parameters:
     * name - {String} Name of the Layer
     * options - {Object} Hashtable of extra options to tag onto the layer
     */
    initialize: function (name, options) {
        OpenLayers.Layer.prototype.initialize.apply(this, arguments);

        this.canvas = options.canvas[0];
        var canvas = options.canvas[0];

        // For some reason OpenLayers.Layer.setOpacity assumes there is
        // an additional div between the layer's div and its contents.
        var sub = document.createElement('div');
        sub.appendChild(canvas);
        this.div.appendChild(sub);

        this.graph = options.graph;
        if (!graph) {
            return;
        }

        var render_map = options.render_map;

        var stiffness = options.stiffness || 600.0;
        var repulsion = options.repulsion || 600.0;
        var damping = options.damping || 0.4;

        canvas.width = window.innerWidth - 100;
        this.canvas.width = window.innerWidth - 100;
        this.original_width = window.innerWidth - 100;
        canvas.height = window.innerHeight - 250;
        this.canvas.height = window.innerHeight - 250;
        this.original_height = window.innerHeight - 250;
        var ctx = canvas.getContext("2d");
        var layout = new Layout.ForceDirected(graph, stiffness, repulsion, damping);

        // calculate bounding box of graph layout.. with ease-in
        var currentBB = layout.getBoundingBox();
        var targetBB = {
            bottomleft: new Vector(-2, -2),
            topright: new Vector(2, 2)
        };

        // auto adjusting bounding box
        Layout.requestAnimationFrame(function adjust() {
            targetBB = layout.getBoundingBox();
            // current gets 20% closer to target every iteration
            currentBB = {
                bottomleft: currentBB.bottomleft.add(targetBB.bottomleft.subtract(currentBB.bottomleft).divide(10)),
                topright: currentBB.topright.add(targetBB.topright.subtract(currentBB.topright).divide(10))
            };

            Layout.requestAnimationFrame(adjust);
        });

        // convert to/from screen coordinates	  
        toScreen = function (p) {
            var size = currentBB.topright.subtract(currentBB.bottomleft);
            var sx = p.subtract(currentBB.bottomleft).divide(size.x).x * canvas.width;
            var sy = p.subtract(currentBB.bottomleft).divide(size.y).y * canvas.height;
            return new Vector(sx, sy);
        };

        fromScreen = function (s) {
            var size = currentBB.topright.subtract(currentBB.bottomleft);
            var px = ((s.x / canvas.width) * size.x) + currentBB.bottomleft.x;
            var py = ((s.y / canvas.height) * size.y) + currentBB.bottomleft.y;
            return new Vector(px, py);
        };

        // half-assed drag and drop
        var selected = null;
        var nearest = null;
        var dragged = null;
        var lastSelected = null;

        jQuery(canvas).mousedown(function (e) {
            jQuery('.actions').hide();

            var pos = jQuery(this).offset();
            var p = fromScreen({
                x: e.pageX - pos.left,
                y: e.pageY - pos.top
            });
            prevX = p.x;
            prevY = p.y;
            selected = nearest = dragged = layout.nearest(p);
            if (selected.node !== null) {
                dragged.point.m = 10000.0;
            }
            renderer.start();
        });

        jQuery(canvas).mousemove(function (e) {
            var pos = jQuery(this).offset();
            var p = fromScreen({
                x: e.pageX - pos.left,
                y: e.pageY - pos.top
            });

            if (dragged !== null && dragged.node !== null) {
                dragged.point.p.x = p.x;
                dragged.point.p.y = p.y;

                // disable dragging of canvas
                for (var i = 0; i < render_map.controls.length; i++) {
                    if (render_map.controls[i].displayClass == "olControlNavigation") {
                        render_map.controls[i].deactivate();
                    }
                }

                $(this).addClass('noclick');

            }
            renderer.start();
        });

        jQuery(canvas).bind('mouseup', function (e) {
            dragged = null;
            prevX = prevY = null;
            for (var i = 0; i < render_map.controls.length; i++) {
                if (render_map.controls[i].displayClass == "olControlNavigation") {
                    render_map.controls[i].activate();
                }
            }
        })

        // node selection
        jQuery(canvas).click(function (e) {
            var pos = jQuery(this).offset();
            var p = fromScreen({
                x: e.pageX - pos.left,
                y: e.pageY - pos.top
            });
            selected = layout.nearest(p);
            if ($(this).hasClass('noclick')) {
                $(this).removeClass('noclick');
            } else {
                if (selected.node !== null) // if a node is selected
                {
                    if (selected.node.data.shape.indexOf("highlighted-") == -1) // if the selected node is not highlighted
                    {
                        if (lastSelected != null) {
                            var last = graph.filterNode(lastSelected);
                            var shape = last.nodes[0].data.shape;
                            last.nodes[0].data.shape = shape.replace("highlighted-", "");
                            graph.merge(last);
                        }
                        $("#details-dialog").dialog('close');
                        var highlighted = graph.filterNode(selected.node);
                        var shape = highlighted.nodes[0].data.shape;
                        highlighted.nodes[0].data.shape = "highlighted-" + shape;
                        graph.merge(highlighted);
                        $("#details-dialog").dialog({
                            title: selected.node.data.label,
                            height: 600,
                            width: 600,
                            position: [e.pageX + 25, e.pageY + 25],
                            open: function (event, ui) {
                                if (selected.node.data.pid != "null") {
                                    var url = "http://localhost:8000/repo/objects/" + selected.node.data.pid + "/details/ #dialog-content";
                                    $('#loader').show();
                                    $('#msg').hide();
                                    $('#msg').load(url, function () {
                                        $('#loader').hide();
                                        $('#msg').show();
                                        $("#viewLink").button().click(function () {
                                            $('#viewer-dialog').dialog('close');
                                            $("#viewer-dialog").dialog({
                                                title: "Viewing: " + $("#title").text(),
                                                height: 600,
                                                width: 600,
                                                open: function (event, ui) {
                                                    var url = "http://localhost:8000/repo/objects/" + $("#viewLink").attr("value") + "/view/";
                                                    $('#viewer').load(url);
                                                }
                                            });
                                        });
                                        $("#downloadLink").button();
                                    });
                                } else {
                                    $('#msg').html("<h3>Controller Object</h3>");
                                }
                            },
                            close: function (event, ui) {
                                var deselected = graph.filterNode(selected.node);
                                var shape = deselected.nodes[0].data.shape;
                                deselected.nodes[0].data.shape = shape.replace("highlighted-", "");
                                graph.merge(deselected);
                            }
                        })
                    } else { // un-highlight the node
                        var deselected = graph.filterNode(selected.node);
                        var shape = deselected.nodes[0].data.shape;
                        deselected.nodes[0].data.shape = shape.replace("highlighted-", "");
                        graph.merge(deselected);
                    }
                    lastSelected = selected.node;
                }
            }
        });

        Node.prototype.getWidth = function () {
            var text = typeof (this.data.label) !== 'undefined' ? this.data.label : this.id;
            if (this._width && this._width[text]) return this._width[text];

            ctx.save();
            ctx.font = "16px Verdana, sans-serif";
            var width = ctx.measureText(text).width + 10;
            ctx.restore();

            this._width || (this._width = {});
            this._width[text] = width;

            return width;
        };

        Node.prototype.getRadius = function () {
            return 20;
        };

        Node.prototype.getHeight = function () {
            return 20;
        };

        var renderer = new Renderer(1, layout,

        function clear() {
            ctx.fillStyle = '#ffffff';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
        },

        function drawEdge(edge, p1, p2) {

            var zoom_levels = new Array();
            zoom_levels[0] = 1.00;
            zoom_levels[1] = 1.05;
            zoom_levels[2] = 1.10;
            zoom_levels[3] = 1.15;
            zoom_levels[4] = 1.20;

            var zoom = zoom_levels[render_map.zoom];

            var x1 = toScreen(p1).x;
            var y1 = toScreen(p1).y;
            var x2 = toScreen(p2).x;
            var y2 = toScreen(p2).y;

            var direction = new Vector(x2 - x1, y2 - y1);
            var normal = direction.normal().normalise();

            var from = graph.getEdges(edge.source, edge.target);
            var to = graph.getEdges(edge.target, edge.source);

            var total = from.length + to.length;

            // Figure out edge's position in relation to other edges between the same nodes
            var n = 0;
            for (var i = 0; i < from.length; i++) {
                if (from[i].id === edge.id) {
                    n = i;
                }
            }

            var spacing = 6.0;

            // Figure out how far off centre the line should be drawn
            var offset = normal.multiply(-((total - 1) * spacing) / 2.0 + (n * spacing));

            var s1 = toScreen(p1).add(offset);
            var s2 = toScreen(p2).add(offset);

            var boxWidth = edge.target.getRadius() * zoom;
            var boxHeight = edge.target.getRadius() * zoom;

            var intersection = intersect_line_box(s1, s2, {
                x: x2 - boxWidth / 2.0,
                y: y2 - boxHeight / 2.0
            }, boxWidth, boxHeight);

            if (!intersection) {
                intersection = s2;
            }

            var stroke = typeof (edge.data.color) !== 'undefined' ? edge.data.color : '#000000';

            var arrowWidth;
            var arrowLength;

            var weight = typeof (edge.data.weight) !== 'undefined' ? edge.data.weight : 1.0;

            ctx.lineWidth = Math.max(weight * 2, 0.1) * zoom;
            arrowWidth = (4 + ctx.lineWidth) * zoom;
            arrowLength = 16 * zoom;

            var directional = typeof (edge.data.directed) !== 'undefined' ? edge.data.directed : true;

            // line
            var lineEnd;
            if (directional) {
                lineEnd = intersection.subtract(direction.normalise().multiply(arrowLength * 0.5));
            } else {
                lineEnd = s2;
            }

            ctx.strokeStyle = stroke;
            ctx.beginPath();
            ctx.moveTo(s1.x, s1.y);
            ctx.lineTo(lineEnd.x, lineEnd.y);
            ctx.stroke();

            // arrow
            if (directional) {
                ctx.save();
                ctx.fillStyle = stroke;
                ctx.translate(intersection.x, intersection.y);
                ctx.rotate(Math.atan2(y2 - y1, x2 - x1));
                ctx.beginPath();
                ctx.moveTo(-arrowLength, arrowWidth);
                ctx.lineTo(0, 0);
                ctx.lineTo(-arrowLength, -arrowWidth);
                ctx.lineTo(-arrowLength * 0.8, -0);
                ctx.closePath();
                ctx.fill();
                ctx.restore();
            }

            // now adjust the label placement
            ctx.save();
            ctx.fillStyle = '#00f';
            var x = parseInt((x1 + x2 - (edge.data.text.length * 5)) / 2);
            var y = parseInt((y1 + y2) / 2);
            ctx.fillText(edge.data.text, x, y);
            ctx.restore();
        },

        function drawNode(node, p) {

            var zoom_levels = new Array();
            zoom_levels[0] = 1.00;
            zoom_levels[1] = 1.05;
            zoom_levels[2] = 1.10;
            zoom_levels[3] = 1.15;
            zoom_levels[4] = 1.20;

            var zoom = zoom_levels[render_map.zoom];

            var s = toScreen(p);

            ctx.save();

            ctx.textAlign = "left";
            ctx.textBaseline = "top";
            ctx.font = "12px Verdana, sans-serif";
            ctx.fillStyle = "rgb(0,0,0)";
            ctx.fillText(node.data.label, s.x - (node.data.label.length * 5) / 2, s.y + 20 * zoom);

            ctx.fillStyle = "#FFFFFF";
            ctx.strokeStyle = "#000000";

            switch (node.data.shape) {
            case "circle":
                ctx.beginPath();
                ctx.arc(s.x, s.y, 15 * zoom, 0, Math.PI * 2, true);
                ctx.closePath();
                ctx.fill();
                ctx.stroke();
                break;
            case "highlighted-circle":
                ctx.beginPath();
                ctx.arc(s.x, s.y, 15 * zoom, 0, Math.PI * 2, true);
                ctx.closePath();
                ctx.stroke();
                ctx.fill();
                ctx.lineWidth = 3;
                ctx.strokeStyle = "#f00";
                ctx.beginPath();
                ctx.arc(s.x, s.y, 20 * zoom, 0, Math.PI * 2, true);
                ctx.stroke();
                ctx.closePath();
                break;
            case "square":
                ctx.strokeRect(s.x - 15, s.y - 15, 2 * 15 * zoom, 2 * 15 * zoom);
                ctx.fillRect(s.x - 15, s.y - 15, 2 * 15 * zoom, 2 * 15 * zoom);
                break;
            case "highlighted-square":
                ctx.strokeRect(s.x - 15, s.y - 15, 2 * 15 * zoom, 2 * 15 * zoom);
                ctx.fillRect(s.x - 15, s.y - 15, 2 * 15 * zoom, 2 * 15 * zoom);
                ctx.lineWidth = 3;
                ctx.strokeStyle = "#f00";
                ctx.strokeRect(s.x - 20, s.y - 20, 2 * 20 * zoom, 2 * 20 * zoom);
                break;
            case "hexagon":
                ctx.translate(s.x, s.y);
                ctx.beginPath();
                var points = get_points(15 * zoom, 6);
                for (var idx = 0; idx < points.length; idx++) {
                    if (idx == 0) ctx.moveTo(points[idx][0], points[idx][1]);
                    else ctx.lineTo(points[idx][0], points[idx][1]);
                }
                ctx.closePath();
                ctx.fill();
                ctx.stroke();
                break;
            case "highlighted-hexagon":
                ctx.translate(s.x, s.y);
                ctx.beginPath();
                var points = get_points(15 * zoom, 6);
                for (var idx = 0; idx < points.length; idx++) {
                    if (idx == 0) ctx.moveTo(points[idx][0], points[idx][1]);
                    else ctx.lineTo(points[idx][0], points[idx][1]);
                }
                ctx.closePath();
                ctx.fill();
                ctx.stroke();
                ctx.lineWidth = 3;
                ctx.strokeStyle = "#f00";
                ctx.beginPath();
                points = get_points(20 * zoom_levels[zoom], 6);
                for (idx = 0; idx < points.length; idx++) {
                    if (idx == 0) ctx.moveTo(points[idx][0], points[idx][1]);
                    else ctx.lineTo(points[idx][0], points[idx][1]);
                }
                ctx.closePath();
                ctx.stroke();
                break;
            default:
                break;
            }

            ctx.restore();
        }

        );

        this.renderer = renderer;

        renderer.start();

        // calculates polygon points


        function get_points(radius, number_of_points) {
            var result = [];
            var delta_theta = 2.0 * Math.PI / number_of_points;
            var theta = 0;

            for (var i = 0; i < number_of_points; i++) {
                x = (radius * Math.cos(theta));
                y = (radius * Math.sin(theta));
                result.push([x, y]);
                theta += delta_theta;
            }
            return result;
        }

        // helpers for figuring out where to draw arrows


        function intersect_line_line(p1, p2, p3, p4) {
            var denom = ((p4.y - p3.y) * (p2.x - p1.x) - (p4.x - p3.x) * (p2.y - p1.y));

            // lines are parallel
            if (denom === 0) {
                return false;
            }

            var ua = ((p4.x - p3.x) * (p1.y - p3.y) - (p4.y - p3.y) * (p1.x - p3.x)) / denom;
            var ub = ((p2.x - p1.x) * (p1.y - p3.y) - (p2.y - p1.y) * (p1.x - p3.x)) / denom;

            if (ua < 0 || ua > 1 || ub < 0 || ub > 1) {
                return false;
            }

            return new Vector(p1.x + ua * (p2.x - p1.x), p1.y + ua * (p2.y - p1.y));
        }

        function intersect_line_box(p1, p2, p3, w, h) {
            var tl = {
                x: p3.x,
                y: p3.y
            };
            var tr = {
                x: p3.x + w,
                y: p3.y
            };
            var bl = {
                x: p3.x,
                y: p3.y + h
            };
            var br = {
                x: p3.x + w,
                y: p3.y + h
            };

            var result;
            if (result = intersect_line_line(p1, p2, tl, tr)) {
                return result;
            } // top
            if (result = intersect_line_line(p1, p2, tr, br)) {
                return result;
            } // right
            if (result = intersect_line_line(p1, p2, br, bl)) {
                return result;
            } // bottom
            if (result = intersect_line_line(p1, p2, bl, tl)) {
                return result;
            } // left
            return false;
        }

    },

    /** 
     * Method: moveTo
     *
     * Parameters:
     * bounds - {<OpenLayers.Bounds>} 
     * zoomChanged - {Boolean} 
     * dragging - {Boolean} 
     */
    moveTo: function (bounds, zoomChanged, dragging) {

        OpenLayers.Layer.prototype.moveTo.apply(this, arguments);

        var zoom_levels = new Array();
        zoom_levels[0] = 1.00;
        zoom_levels[1] = 1.10;
        zoom_levels[2] = 1.20;
        zoom_levels[3] = 1.30;
        zoom_levels[4] = 1.50;

        if (zoomChanged) {
            this.canvas.width = this.original_width * zoom_levels[this.map.zoom];
            this.canvas.height = this.original_height * zoom_levels[this.map.zoom];
            this.renderer.start();
        }
    },

    CLASS_NAME: 'NodeGraph.Layer'

});
