window.monitor = window.monitor || {};

monitor.table = '<table class="table"><thead><tr><th>Metric</th><th>Value</th><th></th></tr></thead><tbody id="monbody">{{#metrics}}<tr id="{{name}}"><td>{{name}}</td><td>{{value}}</td><td><i class="icon-flag" style="visibility:hidden"></i></td></tr>{{/metrics}}</tbody></table>';

monitor.row = '<tr id="{{name}}"><td>{{name}}</td><td>{{value}}</td><td><i id="icon.{{name}}" class="active icon-flag" style="font-color:red"></i></td></tr>';

monitor.updateTable = function(element, data) {
	if (element) {
		var result = [];
		var i = 0;
		for ( var item in data) {
			if (!/.*\.stomp\./.test(data[item].name)) {
				result[i++] = data[item];
			} else {
				console.log("Not showing: " + JSON.stringify(data[item]));
			}
		}
		result.sort(function(one, two) {
			if (one == two)
				return 0;
			return one.name > two.name ? 1 : -1;
		});
		element.mustache('monitorTable', {
			metrics : result
		}, {
			method : "html"
		});
	}
};

monitor.updateRow = function(parent, element, data, fader) {
	if (element) {
		element.remove();
		parent.mustache('monitorRow', data, {
			method : "prepend"
		});
		fader();
	}
};

monitor.scocket = null;

monitor.staticUpdate = function(element, endpoint) {
	if (element) {
		$.get(endpoint, function(input) {
			var data = [], i = 0;
			for ( var key in input) {
				data[i++] = {
					name : key,
					value : input[key]
				};
			}
			monitor.updateTable(element, data);
		});
	}
};

monitor.open = function(ws, metrics) {
	var element = $('#js-monitor');
	if (!element) {
		return

	}
	monitor.staticUpdate(element, metrics);
	monitor.socket = new SockJS(ws);
	var client = Stomp.over(monitor.socket);
	client.connect('guest', 'guest', function(frame) {
		console.log('Connected ' + frame);
		client.subscribe("/topic/metrics/*", function(message) {
			var data = JSON.parse(message.body);
			if (!/.*\.stomp\./.test(data.name)) {
				monitor.updateRow($('#monbody'), $('#'
						+ data.name.replace(/\./g, '\\.')), data, function() {
					$('#icon\\.' + data.name.replace(/\./g, '\\.')).fadeOut(
							'slow');
				});
			}
		});
	});
};

$(window).load(function() {
	$.Mustache.add('monitorTable', $('#monitorTable').html() || monitor.table);
	$.Mustache.add('monitorRow', $('#monitorRow').html() || monitor.row);
	monitor.open("/stomp", "/metrics");
});