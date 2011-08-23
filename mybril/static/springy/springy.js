/**
Copyright (c) 2010 Dennis Hotson

 Permission is hereby granted, free of charge, to any person
 obtaining a copy of this software and associated documentation
 files (the "Software"), to deal in the Software without
 restriction, including without limitation the rights to use,
 copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the
 Software is furnished to do so, subject to the following
 conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 OTHER DEALINGS IN THE SOFTWARE.
*/

/*
 * Modifier: Eric Liao 2011
 */

var Graph = function()
{
	this.nodeSet = {};
	this.nodes = [];
	this.edges = [];
	this.adjacency = {};

	this.nextNodeId = 0;
	this.nextEdgeId = 0;
	this.eventListeners = [];	
};

Node = function(id, data)
{
	this.id = id;
	this.data = typeof(data) !== 'undefined' ? data : {};
};

Edge = function(id, source, target, data)
{
	this.id = id;
	this.source = source;
	this.target = target;
	this.data = typeof(data) !== 'undefined' ? data : {};
};

Graph.prototype.addNode = function(node)
{
	if (typeof(this.nodeSet[node.id]) === 'undefined')
	{
		this.nodes.push(node);
	}

	this.nodeSet[node.id] = node;

	this.notify();
	return node;
};

Graph.prototype.addEdge = function(edge)
{
	var exists = false;
	this.edges.forEach(function(e){
		if (edge.id === e.id) { exists = true; }
	});

	if (!exists)
	{
		this.edges.push(edge);
	}

	if (typeof(this.adjacency[edge.source.id]) === 'undefined')
	{
		this.adjacency[edge.source.id] = {};
	}
	if (typeof(this.adjacency[edge.source.id][edge.target.id]) === 'undefined')
	{
		this.adjacency[edge.source.id][edge.target.id] = [];
	}

	exists = false;
	this.adjacency[edge.source.id][edge.target.id].forEach(function(e){
			if (edge.id === e.id) { exists = true; }
	});

	if (!exists)
	{
		this.adjacency[edge.source.id][edge.target.id].push(edge);
	}

	this.notify();
	return edge;
};

Graph.prototype.newNode = function(data)
{
	var node = new Node(this.nextNodeId++, data);
	this.addNode(node);
	return node;
};

Graph.prototype.newEdge = function(source, target, data)
{
	var edge = new Edge(this.nextEdgeId++, source, target, data);
	this.addEdge(edge);
	return edge;
};

// find the edges from node1 to node2
Graph.prototype.getEdges = function(node1, node2)
{
	if (typeof(this.adjacency[node1.id]) !== 'undefined'
		&& typeof(this.adjacency[node1.id][node2.id]) !== 'undefined')
	{
		return this.adjacency[node1.id][node2.id];
	}

	return [];
};

Graph.prototype.clearGraph = function()
{
  this.nodes.forEach(function(node) {
		  this.removeNode(node);
	}, this);
}

// remove a node and it's associated edges from the graph
Graph.prototype.removeNode = function(node)
{
	if (typeof(this.nodeSet[node.id]) !== 'undefined')
	{
		delete this.nodeSet[node.id];
	}

	for (var i = this.nodes.length - 1; i >= 0; i--)
	{
		if (this.nodes[i].id === node.id)
		{
			this.nodes.splice(i, 1);
		}
	}

	var tmpEdges = this.edges.slice();
	var filtered = [];
	tmpEdges.forEach(function(e) {
		if (e.source.id === node.id || e.target.id === node.id)
		{
			filtered.push(e);
			this.removeEdge(e);
		}
	}, this);
	
	this.notify();
	return filtered;
};


// remove a node and it's associated edges from the graph
Graph.prototype.removeEdge = function(edge)
{
	for (var i = this.edges.length - 1; i >= 0; i--)
	{
		if (this.edges[i].id === edge.id)
		{
			this.edges.splice(i, 1);
		}
	}
	
	for (var x in this.adjacency)
	{
		for (var y in this.adjacency[x])
		{
			var edges = this.adjacency[x][y];

			for (var j=edges.length - 1; j>=0; j--)
			{
				if (this.adjacency[x][y][j].id === edge.id)
				{
					this.adjacency[x][y].splice(j, 1);
				}
			}
		}
	}

	this.notify();
};

/* Load JSON data into the current graph */
Graph.prototype.loadData = function(data)
{
	var nodes = [];
	data.nodes.forEach(function(n) {
		nodes[n.id] = this.addNode(new Node(n.id, n.data));
		//nodes.push(this.addNode(new Node(n.id, n.data)));
	}, this);

	data.edges.forEach(function(e) {
		var from = nodes[e._from];				
		var to = nodes[e._to];

		var id = (e.directed)
			? (id = e.type + "-" + from.id + "-" + to.id)
			: (from.id < to.id) // normalise id for non-directed edges
				? e.type + "-" + from.id + "-" + to.id
				: e.type + "-" + to.id + "-" + from.id;
				
		var edge = this.addEdge(new Edge(id, from, to, e.data));
		edge.data.type = e.type;
	}, this);
};

/* Merge a list of nodes and edges into the current graph. eg.
var o = {
	nodes: [
		{id: 123, data: {type: 'user', userid: 123, displayname: 'aaa'}},
		{id: 234, data: {type: 'user', userid: 234, displayname: 'bbb'}}
	],
	edges: [
		{from: 0, to: 1, type: 'submitted_design', directed: true, data: {weight: }}
	]
}
*/
Graph.prototype.merge = function(data)
{
	var nodes = [];	
	data.nodes.forEach(function(n) {
		nodes[n.id] = this.addNode(new Node(n.id, n.data));
		//nodes.push(this.addNode(new Node(n.id, n.data)));
	}, this);

	data.edges.forEach(function(e) {
		var from = e.source;		
		var to = e.target;

		var id = (e.directed)
			? (id = e.type + "-" + from.id + "-" + to.id)
			: (from.id < to.id) // normalise id for non-directed edges
				? e.type + "-" + from.id + "-" + to.id
				: e.type + "-" + to.id + "-" + from.id;

		var edge = this.addEdge(new Edge(id, from, to, e.data));
		edge.data.type = e.type;
	}, this);
};

Graph.prototype.mergeEdges = function(edges)
{	
	edges.forEach(function(e) {
		var from = e.source;		
		var to = e.target;

		var id = (e.directed)
			? (id = e.type + "-" + from.id + "-" + to.id)
			: (from.id < to.id) // normalise id for non-directed edges
				? e.type + "-" + from.id + "-" + to.id
				: e.type + "-" + to.id + "-" + from.id;

		var edge = this.addEdge(new Edge(id, from, to, e.data));
		edge.data.type = e.type;
	}, this);
};

Graph.prototype.filterNodes = function(shape)
{
	var tmpNodes = this.nodes.slice();
	var o = {};
	var filtered = [];
	var filtered_edges = [];			
	
	tmpNodes.forEach(function(n) {
		if (shape == "circle" || shape == "hexagon" || shape == "square") {
			if (n.data.shape == shape)
			{			
				filtered.push(n);
				filtered_edges = filtered_edges.concat(this.removeNode(n));			
			}
		}
		else {
			if (n.data.label == shape)
			{			
				filtered.push(n);
				filtered_edges = filtered_edges.concat(this.removeNode(n));			
			}			
		}
	}, this);
	o = {nodes: filtered, edges: filtered_edges};
	return o;
};

Graph.prototype.filterNode = function(node)
{
	var o = {};
	var filtered = [];
	var filtered_edges = [];			
				
	filtered.push(node);
	filtered_edges = filtered_edges.concat(this.removeNode(node));			
	o = {nodes: filtered, edges: filtered_edges};
	return o;
};

Graph.prototype.filterEdges = function(text)
{	
	var tmpEdges = this.edges.slice();	
	var filtered_edges = [];	
	tmpEdges.forEach(function(e) {				
		if (e.data.text == text || text == "all_relationships")
		{						
			filtered_edges.push(e);
			this.removeEdge(e);
		}				
	}, this);
	return filtered_edges;
};


Graph.prototype.addGraphListener = function(obj)
{
	this.eventListeners.push(obj);
};

/*
Graph.prototype.notify = function()
{
	this.eventListeners.forEach(function(obj){var layout = new Layouts.ForceDirected(graph, 400.0, 400.0, 0.5);
		obj.graphChanged();
	});
};
*/

Graph.prototype.notify = function() {
	this.eventListeners.forEach(function(obj){
		obj.graphChanged();
	});
};
// -----------
var Layouts = {};
Layouts.ForceDirected = function(graph, stiffness, repulsion, damping)
{
	this.graph = graph;
	this.stiffness = stiffness; // spring stiffness constant
	this.repulsion = repulsion; // repulsion constant
	this.damping = damping; // velocity damping factor

	this.nodePoints = {}; // keep track of points associated with nodes
	this.edgeSprings = {}; // keep track of springs associated with edges

	this.intervalId = null;
};

Layouts.ForceDirected.prototype.point = function(node)
{
	if (typeof(this.nodePoints[node.id]) === 'undefined')
	{
		var mass = typeof(node.data.mass) !== 'undefined' ? node.data.mass : 1.0;
		this.nodePoints[node.id] = new Layouts.ForceDirected.Point(Vector.random(), mass);
	}

	return this.nodePoints[node.id];
};

Layouts.ForceDirected.prototype.spring = function(edge)
{
	if (typeof(this.edgeSprings[edge.id]) === 'undefined')
	{
		var length = typeof(edge.data.length) !== 'undefined' ? edge.data.length : 1.0;

		var existingSpring = false;

		var from = this.graph.getEdges(edge.source, edge.target);
		from.forEach(function(e){
			if (existingSpring === false && typeof(this.edgeSprings[e.id]) !== 'undefined') {
				existingSpring = this.edgeSprings[e.id];
			}
		}, this);

		if (existingSpring !== false) {
			return new Layouts.ForceDirected.Spring(existingSpring.point1, existingSpring.point2, 0.0, 0.0);
		}

		var to = this.graph.getEdges(edge.target, edge.source);
		from.forEach(function(e){
			if (existingSpring === false && typeof(this.edgeSprings[e.id]) !== 'undefined') {
				existingSpring = this.edgeSprings[e.id];
			}
		}, this);

		if (existingSpring !== false) {
			return new Layouts.ForceDirected.Spring(existingSpring.point2, existingSpring.point1, 0.0, 0.0);
		}

		this.edgeSprings[edge.id] = new Layouts.ForceDirected.Spring(
			this.point(edge.source), this.point(edge.target), length, this.stiffness
		);
	}

	return this.edgeSprings[edge.id];
};

// callback should accept two arguments: Node, Point
Layouts.ForceDirected.prototype.eachNode = function(callback)
{
	var t = this;
	this.graph.nodes.forEach(function(n){
		callback.call(t, n, t.point(n));
	});
};

// callback should accept two arguments: Edge, Spring
Layouts.ForceDirected.prototype.eachEdge = function(callback)
{
	var t = this;
	this.graph.edges.forEach(function(e){
		callback.call(t, e, t.spring(e));
	});
};

// callback should accept one argument: Spring
Layouts.ForceDirected.prototype.eachSpring = function(callback)
{
	var t = this;
	this.graph.edges.forEach(function(e){
		callback.call(t, t.spring(e));
	});
};


// Physics stuff
Layouts.ForceDirected.prototype.applyCoulombsLaw = function()
{
	this.eachNode(function(n1, point1) {
		this.eachNode(function(n2, point2) {
			if (point1 !== point2)
			{
				var d = point1.p.subtract(point2.p);
				var distance = d.magnitude() + 1.0;
				var direction = d.normalise();

				// apply force to each end point
				point1.applyForce(direction.multiply(this.repulsion).divide(distance * distance * 0.5));
				point2.applyForce(direction.multiply(this.repulsion).divide(distance * distance * -0.5));
			}
		});
	});
};

Layouts.ForceDirected.prototype.applyHookesLaw = function()
{
	this.eachSpring(function(spring){
		var d = spring.point2.p.subtract(spring.point1.p); // the direction of the spring
		var displacement = spring.length - d.magnitude();
		var direction = d.normalise();

		// apply force to each end point
		spring.point1.applyForce(direction.multiply(spring.k * displacement * -0.5));
		spring.point2.applyForce(direction.multiply(spring.k * displacement * 0.5));
	});
};

Layouts.ForceDirected.prototype.attractToCentre = function()
{
	this.eachNode(function(node, point) {
		var direction = point.p.multiply(-1.0);
		point.applyForce(direction.multiply(this.repulsion / 50.0));
	});
};


Layouts.ForceDirected.prototype.updateVelocity = function(timestep)
{
	this.eachNode(function(node, point) {
		point.v = point.v.add(point.f.multiply(timestep)).multiply(this.damping);
		point.f = new Vector(0,0);
	});
};

Layouts.ForceDirected.prototype.updatePosition = function(timestep)
{
	this.eachNode(function(node, point) {
		point.p = point.p.add(point.v.multiply(timestep));
	});
};

Layouts.ForceDirected.prototype.totalEnergy = function(timestep)
{
	var energy = 0.0;
	this.eachNode(function(node, point) {
		var speed = point.v.magnitude();
		energy += speed * speed;
	});

	return energy;
};

var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }; // stolen from coffeescript, thanks jashkenas! ;-)

Layouts.requestAnimationFrame = __bind(window.requestAnimationFrame ||
	window.webkitRequestAnimationFrame ||
	window.mozRequestAnimationFrame ||
	window.oRequestAnimationFrame ||
	window.msRequestAnimationFrame ||
	function(callback, element) {
		window.setTimeout(callback, 100);
	}, window);

// start simulation
Layouts.ForceDirected.prototype.start = function(interval, render, done)
{
	var t = this;

	if (this.intervalId !== null) {
		return; // already running
	}

	this.intervalId = setInterval(function step() {
		t.applyCoulombsLaw();
		t.applyHookesLaw();
		t.attractToCentre();
		t.updateVelocity(0.03);
		t.updatePosition(0.03);

		if (typeof(render) !== 'undefined') { render(); }

		// stop simulation when energy of the system goes below a threshold
		if (t.totalEnergy() < 0.1) {
			t._started = false;
			if (typeof(done) !== 'undefined') { done(); }
		} else {
			Layouts.requestAnimationFrame(step);
		}
		
	}, interval);
};

// Find the nearest point to a particular position
Layouts.ForceDirected.prototype.nearest = function(pos)
{
	var min = {node: null, point: null, distance: null};
	var t = this;
	this.graph.nodes.forEach(function(n){
		var point = t.point(n);
		var distance = point.p.subtract(pos).magnitude();

		if (distance < 0.1)
		{
			min = {node: n, point: point, distance: distance};
		}
	});

	return min;
};

// returns [bottomleft, topright]
Layouts.ForceDirected.prototype.getBoundingBox = function()
{
	var bottomleft = new Vector(-2,-2);
	var topright = new Vector(2,2);

	this.eachNode(function(n, point) {
		if (point.p.x < bottomleft.x) {
			bottomleft.x = point.p.x;
		}
		if (point.p.y < bottomleft.y) {
			bottomleft.y = point.p.y;
		}
		if (point.p.x > topright.x) {
			topright.x = point.p.x;
		}
		if (point.p.y > topright.y) {
			topright.y = point.p.y;
		}
	});

	var padding = topright.subtract(bottomleft).multiply(0.07); // 5% padding

	return {bottomleft: bottomleft.subtract(padding), topright: topright.add(padding)};
};


// Vector
Vector = function(x, y)
{
	this.x = x;
	this.y = y;
};

Vector.random = function()
{
	return new Vector(10.0 * (Math.random() - 0.5), 10.0 * (Math.random() - 0.5));
};

Vector.prototype.add = function(v2)
{
	return new Vector(this.x + v2.x, this.y + v2.y);
};

Vector.prototype.subtract = function(v2)
{
	return new Vector(this.x - v2.x, this.y - v2.y);
};

Vector.prototype.multiply = function(n)
{
	return new Vector(this.x * n, this.y * n);
};

Vector.prototype.divide = function(n)
{
	return new Vector(this.x / n, this.y / n);
};

Vector.prototype.magnitude = function()
{
	return Math.sqrt(this.x*this.x + this.y*this.y);
};

Vector.prototype.normal = function()
{
	return new Vector(-this.y, this.x);
};

Vector.prototype.normalise = function()
{
	return this.divide(this.magnitude());
};

// Point
Layouts.ForceDirected.Point = function(position, mass)
{
	this.p = position; // position
	this.m = mass; // mass
	this.v = new Vector(0, 0); // velocity
	this.f = new Vector(0, 0); // force
};

Layouts.ForceDirected.Point.prototype.applyForce = function(force)
{
	this.f = this.f.add(force.divide(this.m));
};

// Spring
Layouts.ForceDirected.Spring = function(point1, point2, length, k)
{
	this.point1 = point1;
	this.point2 = point2;
	this.length = length; // spring length at rest
	this.k = k; // spring constant (See Hooke's law) .. how stiff the spring is
};

// Layouts.ForceDirected.Spring.prototype.distanceToPoint = function(point)
// {
// 	// hardcore vector arithmetic.. ohh yeah!
// 	// .. see http://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment/865080#865080
// 	var n = this.point2.p.subtract(this.point1.p).normalise().normal();
// 	var ac = point.p.subtract(this.point1.p);
// 	return Math.abs(ac.x * n.x + ac.y * n.y);
// };

// Renderer handles the layout rendering loop
function Renderer(interval, layout, clear, drawEdge, drawNode)
{
	this.interval = interval;
	this.layout = layout;
	this.clear = clear;
	this.drawEdge = drawEdge;
	this.drawNode = drawNode;

	this.layout.graph.addGraphListener(this);
}

Renderer.prototype.graphChanged = function(e)
{
	this.start();
};

Renderer.prototype.start = function()
{
	var t = this;
	this.layout.start(100, function render() {
		t.clear();

		t.layout.eachEdge(function(edge, spring) {
			t.drawEdge(edge, spring.point1.p, spring.point2.p);
		});

		t.layout.eachNode(function(node, point) {
			t.drawNode(node, point.p);
		});
	});	
};
