$(function() {

  // on click
  $('#submit').on('click', function() {

    // collecting the data from the form
    const userID = $('#userID').val();
    const activity_type = $('#activity_type').val();
    const startDate = $('#startDate').val();
    const endDate = $('#endDate').val();
    const frequency = $('#frequency').val();
    
    // checking if all fields are filled
    const divShowData = document.getElementById('error');
    if (userID == '') {
      divShowData.innerHTML = "Please enter UserID";
      return;
    } else if (startDate == '') {
      divShowData.innerHTML = "Please enter Beginning Date";
      return;
    } else if (endDate == '') {
      divShowData.innerHTML = "Please enter End Date";
      return;
    } 

    //SPARQL
    QUERY = SPARQLquery(userID, activity_type, startDate, endDate, frequency);

    // AJAX Request
    var REPOSITORY = 'http://localhost:7200/repositories/ActivityObservation';
    $.ajax({
      type: 'GET',
      url: REPOSITORY,
      data: {
        query: QUERY
      },
      crossDomain: true,
      offset: 0,
      success: function (data) {
        resultProccessing(data, activity_type, frequency);
      },
      error: function () {
        console.log('Fail');
        document.getElementById('error').innerHTML = "";
        divShowData.innerHTML = "There has been a problem connecting to the database.";
      }
    });
  });
});

function SPARQLquery(userID, activity_type, startDate, endDate, frequency) {
  const PREFIXES = 'PREFIX owl: <http://www.w3.org/2002/07/owl#>\n' +
  'PREFIX act: <http://www.semanticweb.org/ActivityObservation#> \n' +
  'PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n' +
  'PREFIX xml: <http://www.w3.org/XML/1998/namespace> \n' +
  'PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n' +
  'PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n' +
  'PREFIX sosa: <http://www.w3.org/ns/sosa/>';

  // defying data type (activity type and frequency)                       
  var obs_type = '';
  if (activity_type == 'Heart Rate') {
    if (frequency == 'Daily') {
      obs_type = 'DailyHeartRate';
    } else {
      obs_type = 'HourlyHeartRate';
    }
  } else if (activity_type == 'Steps') {
    if (frequency == 'Daily') {
      obs_type = 'DailySteps';
    } else {
      obs_type = 'HourlySteps';
    }
  } else if (activity_type == 'Calories') {
    if (frequency == 'Daily') {
      obs_type = 'DailyCalories';
    } else {
      obs_type = 'HourlyCalories';
    }
  } else if (activity_type == 'Sleep') {
    obs_type = 'DailySleep';
  } else if (activity_type == 'Weight') {
    obs_type = 'DailyWeight';
  } else {
    obs_type = 'DailyBMI';
  }

  // buliding the SPARQL query
  var QUERY = PREFIXES + 
              'SELECT ?date ?value '+
              `WHERE { act:${userID} act:hasObservation ?obs.`;

  QUERY += `?obs a act:${obs_type}.` +
            '?obs act:time ?date.' +
            '?obs act:measured ?value.' ;

  // defying the date conditions
  if (frequency=='Daily') {
    QUERY += `FILTER((?date>="${startDate}"^^xsd:dateTime) && (?date<="${endDate}"^^xsd:dateTime)).} `;
  } else {
    QUERY += `FILTER((?date>="${startDate}T00:00:00"^^xsd:dateTime) && (?date<="${endDate}T00:00:00"^^xsd:dateTime)).} `;
  }

  return QUERY;
}

function resultProccessing(data, activity_type, frequency) {
  document.getElementById('chart').innerHTML = "";
  document.getElementById('error').innerHTML = "";
  var resultData = data.split('\r\n');
  var pairs = [];
  for(i=0; i<resultData.length-1;i++){
    temp = resultData[i].split(',');
    pairs.push(resultData[i]);
  }
  if (pairs.length==1) {
    const divShowData = document.getElementById('error');
    divShowData.innerHTML = "No data found";
  } else {
    // data
    var bars = [];
    var temp;
    for(let i=1; i<resultData.length-1;i++){
      temp = resultData[i].split(',');
      bars.push({x: temp[0], y: temp[1]});
    }
    // column colors
    var range, legends, colors;
    if (activity_type == 'Heart Rate') {
      range = [{from: 0, to: 60,color: '#FF7900'}, {from: 60, to: 100, color: '#00E396'}, {from: 101, to: 200,color: '#8F0B0B'}];
      legends = ['Low '+activity_type, 'Normal '+activity_type, 'High '+activity_type];
      colors = ['#FF7900', '#00E396', '#8F0B0B'];
    } else if (activity_type == 'Steps') {
      if (frequency=='Daily') {
        range = [{from: 0, to: 5000, color: '#FF7900'}, {from: 5001, to: 999999999999, color: '#00E396'}];
        legends = ['Low '+activity_type, 'Normal '+activity_type];
        colors = ['#FF7900', '#00E396'];
      } else {
        range = [{from: 0, to: 999999999999, color: '#00E396'}];
        legends = [activity_type];
        colors = ['#00E396'];
      }
    } else if (activity_type == 'Calories') {
      range = [{from: 0, to: 999999999999, color: '#00E396'}];
      legends = [activity_type];
      colors = ['#00E396'];
    } else if (activity_type == 'Sleep') {
      range = [{from: 0, to: 419, color: '#FF7900'}, {from: 420, to: 600, color: '#00E396'}, {from: 601, to: 1440,color: '#8F0B0B'}];
      legends = ['Too little '+activity_type, 'Normal '+activity_type, 'Too much '+activity_type];
      colors = ['#FF7900', '#00E396', '#8F0B0B'];
    } else if (activity_type == 'Weight') {
      range = [{from: 0, to: 999999999999, color: '#00E396'}];
      legends = [activity_type];
      colors = ['#00E396'];
    } else {
      range = [{from: 0, to: 17.9,color: '#FF7900'}, {from: 18, to: 30, color: '#00E396'}, {from: 30.01, to: 100,color: '#8F0B0B'}];
      legends = ['Low '+activity_type, 'Normal '+activity_type, 'High '+activity_type];
      colors = ['#FF7900', '#00E396', '#8F0B0B'];
    }

    // dashboard building
    var options = {
      series: [{name: activity_type, data: bars}],
      chart: { height: 350, type: 'bar'},
      plotOptions: {bar: {columnWidth: '60%', colors: {
        ranges: range
      }}},
      dataLabels: {enabled: true},
      legend: { show: true, showForSingleSeries: true,
        customLegendItems: legends, 
        markers: {fillColors: colors}
    }};
    var chart = new ApexCharts(document.querySelector("#chart"), options);
    chart.render();
  }
}



