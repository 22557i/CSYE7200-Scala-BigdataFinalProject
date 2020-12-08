
Circles.create({
	id:           'task-complete',
	radius:       75,
	value:        93,
	maxValue:     100,
	width:        8,
	text:         function(value){return value + '%';},
	colors:       ['#eee', '#1D62F0'],
	duration:     400,
	wrpClass:     'circles-wrp',
	textClass:    'circles-text',
	styleWrapper: true,
	styleText:    true
})

$.notify({
	icon: 'la la-bell',
	title: 'Bootstrap notify',
	message: 'Turning standard Bootstrap alerts into "notify" like notifications',
},{
	type: 'success',
	placement: {
		from: "bottom",
		align: "right"
	},
	time: 1000,
});

// monthlyChart

Chartist.Pie('#monthlyChart', {
	labels: ['Shared room-2.37%', 'Entire room/apt - 51.97%', 'Private room - 45.66%'],
	series: [2.37, 51.97, 45.66]
}, {
	plugins: [
	Chartist.plugins.tooltip()
	]
});

// trafficChart
var chart = new Chartist.Line('#trafficChart', {
	labels: [1, 2, 3, 4, 5, 6, 7],
	series: [
	[5, 9, 7, 8, 5, 3, 5],
	[6, 9, 5, 10, 2, 3, 7],
	[2, 7, 4, 10, 7, 6, 2]
	]
}, {
	plugins: [
	Chartist.plugins.tooltip()
	],
	low: 0,
	height: "245px",
});

// salesChart
var dataSales = {
	labels: ['Williamsburg', 'Bedford-Stuyvesant', 'Harlem', 'Bushwick', 'Upper West Side', 'Hells Kitchen', 'East Village ', 'Upper East Side', 'Crown Heights', 'Midtown'],
	series: [
		[3920,3714,2658,2465,1971,1958,1853,1798,1564,1545]
	]
}

var optionChartSales = {
	plugins: [
	Chartist.plugins.tooltip()
	],
	seriesBarDistance: 10,
	axisX: {
		showGrid: false
	},
	height: "245px",
}

var responsiveChartSales = [
['screen and (max-width: 640px)', {
	seriesBarDistance: 5,
	axisX: {
		labelInterpolationFnc: function (value) {
			return value[0];
		}
	}
}]
];

Chartist.Bar('#salesChart', dataSales, optionChartSales, responsiveChartSales);

$(".mapcontainer").mapael({
	map : {
		name : "world_countries",
		zoom: {
			enabled: true,
			maxLevel: 10
		},
		defaultPlot: {
			attrs: {
				fill: "#004a9b"
				, opacity: 0.6
			}
		}, 
		defaultArea: {
			attrs: {
				fill: "#e4e4e4"
				, stroke: "#fafafa"
			}
			, attrsHover: {
				fill: "#59d05d"
			}
			, text: {
				attrs: {
					fill: "#505444"
				}
				, attrsHover: {
					fill: "#000"
				}
			}
		}
	},
	areas: {
				// "department-56": {
				// 	text: {content: "Morbihan", attrs: {"font-size": 10}},
				// 	tooltip: {content: "<b>Morbihan</b> <br /> Bretagne"}
				// },
				"ID": {
					tooltip: {content: "<b>Indonesia</b> <br /> Tempat Lahir Beta"},
					attrs: {
						fill: "#59d05d"
					}
					, attrsHover: {
						fill: "#59d05d"
					}
				},
				"RU": {
					tooltip: {content: "<b>Russia</b>"},
					attrs: {
						fill: "#59d05d"
					}
					, attrsHover: {
						fill: "#59d05d"
					}					
				},
				"US": {
					tooltip: {content: "<b>United State</b>"},
					attrs: {
						fill: "#59d05d"
					}
					, attrsHover: {
						fill: "#59d05d"
					}					
				},
				"AU": {
					tooltip: {content: "<b>Australia</b>"},
					attrs: {
						fill: "#59d05d"
					}
					, attrsHover: {
						fill: "#59d05d"
					}					
				}
			},
		});
var map, geocoder,ismarker=0,marker;
function initialize() {
	var latlng = new google.maps.LatLng(39.904214, 116.407413);
	var options = {
		zoom: 11,
		center: latlng,
		disableDefaultUI: true,
		panControl: true,
		zoomControl: true,
		mapTypeControl: true,
		scaleControl: true,
		streetViewControl: false,
		overviewMapControl: true,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	map = new google.maps.Map(document.getElementById("map_canvas"), options);
	geocoder = new google.maps.Geocoder();
	google.maps.event.addListener(map, 'click', function(event) {
		if(ismarker==1){
			ismarker=2;
			placeMarker(event.latLng);
		}
	});
}
function placeMarker(location) {
	marker = new google.maps.Marker({
		position: location,
		map: map,
		draggable:true
	});
	var infowindow = new google.maps.InfoWindow({
		content: "标记是可以拖动的"
	});
	google.maps.event.addListener(marker, 'click', function() {
		infowindow.open(marker.get('map'), marker);
	});
}
function search(address) {
	if (!map) return;
	geocoder.geocode({address : address}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			map.setZoom(11);
			map.setCenter(results[0].geometry.location);
		} else {
			alert("Invalid address: " + address);
		}
	});
}
$("#addmarker").click(function(){
	if(ismarker==2){
		art.dialog({
			title:'添加失败',
			content:'标记已经存在或者即将存在,请删除后标记后重新添加',
			lock:true,
			ok:true,
			cancel:true
		})
	}else{
		art.dialog({
			title:'添加标记',
			content:'现在单击地图任意一点即添加标记,<font color=red>标记是可拖动</font>,请把标记移到合适的位置',
			lock:true,
			ok:function(){
				ismarker=1;
			},
			cancel:true
		})
		ismarker=0;
	}
})
$("#deletemarker").click(function(){

	art.dialog({
		title:'删除标记',
		content:'你已经删除了标记',
		lock:true,
		ok:function(){
			marker.setMap(null);
			ismarker=0;
		},
		cancel:true
	})

})