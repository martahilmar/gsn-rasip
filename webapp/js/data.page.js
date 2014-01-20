/* XML object loaded from the REQUEST_LIST_OF_VIRTUAL_SENSORS GSN request */
var lovs = null;
/* Mapping from virtual sensor name to list of fields name */
var virtual_sensor_to_fields = [];
var field_to_virtual_sensor = [];

$.Mustache.addFromDom();

function csv_to_json(tag, callback, format) {
    callback = callback || function () {};
    var old_value = $('#time_format').val();
    $('#download_format').val('csv');
    format = format || 'unix';
    $('#time_format').val(format);
    prepare_form_for_submission(tag);
    $.post('multidata', tag.parents('form').serializeArray(), function (response, text_status, request) {
        var lines = response.split("\n");
        var headers = [];
        var timestamp_idx = -1;
        var vs_name = '';
        var keys = [];
        var nb_elements = 0;
        var datasets = [];
        for (var line_no = 0; line_no < lines.length; line_no++) {
            var line = $.trim(lines[line_no]);
            if (line.length == 0)
                continue;

            if (line[0] == '#') {
                if (line.indexOf("##vsname:") >= 0) { //comment lines
                    vs_name = line.substring(9, line.length);
                    keys = [];
                    headers = [];
                }
                if (line[0] == '#' && line[1] != '#') { //Header
                    headers = line.split(',');
                    for (var i = 0; i < headers.length; i++) {
                        headers[i] = headers[i].replace("#", "");
                        if (headers[i] == 'timed') {
                            timestamp_idx = i;
                        } else {
                            lbl = vs_name + ' (' + headers[i] + ')';
                            datasets[lbl] = {
                                label: lbl,
                                data: []
                            };
                            keys.push(lbl);
                        }
                    }
                }
            } else { // data line, at this stage we should have a header varialbe already set.
                var data = line.split(',');
                var timestamp = data[timestamp_idx];
                for (i = 0; i < headers.length; i++) {
                    if (i != timestamp_idx && data[i] != undefined) {
                        if (format == 'unix') {
                            datasets[keys[i]].data.push([parseInt(timestamp), parseFloat(data[i])]);
                        } else {
                            datasets[keys[i]].data.push([timestamp, parseFloat(data[i])]);
                        }
                        nb_elements++;
                    }
                }
            }
        }
        $('#time_format').val(old_value);
        callback(datasets, nb_elements);
    });
}



    /******************************************************/
    /*                  CHARTING PART                      */
    /******************************************************/

    function drawChart(datasets, nbElements) {
        var full_dataset = datasets;
        var selected_datasets = [];
        var plot;
        var i = 0;
        var options = {
            legend: {
                show: false
            },
            lines: {
                show: true
            },
            points: {
                show: false
            },
            selection: {
                mode: "xy"
            },
            xaxis: {
                mode: "time",
                timeformat: "%H:%M:%S %y/%m/%d"
            }
        };

        for (var d in datasets) {
            selected_datasets.push(full_dataset[d]);
            full_dataset[d].color = i++; // hard-code color indices to prevent them from shifting colors dynamically
        }

        function plotAccordingToChoices() {
            if (selected_datasets.length > 0) {
                plot.setData(selected_datasets);
                plot.setupGrid();
                plot.draw();
            }
        }
        plot = $.plot($(".gsn_chart"), [], options);

        var overview = $.plot($(".overview"), selected_datasets, {
            lines: {
                show: true,
                lineWidth: 1
            },
            legend: {
                show: true,
                container: $(".overviewLegend")
            },
            grid: {
                color: "#999"
            },
            shadowSize: 0,
            xaxis: {
                ticks: [],
                mode: "time"
            },
            yaxis: {
                ticks: []
            },
            selection: {
                mode: "xy"
            }
        });

        // now connect the two

        $(".gsn_chart").bind("plotselected", function (event, ranges) {
            // do the zooming

            $(".gsn_chart").empty();
            plot = $.plot($(".gsn_chart"), selected_datasets,

                $.extend(true, {}, options, {
                    xaxis: {
                        min: ranges.xaxis.from,
                        max: ranges.xaxis.to
                    },
                    yaxis: {
                        min: ranges.yaxis.from,
                        max: ranges.yaxis.to
                    }
                }));
            // don't fire event on the overview to prevent eternal loop
            overview.setSelection(ranges, true);
        });

        $(".overview").bind("plotselected", function (event, ranges) {
            plot.setSelection(ranges);
        });
        $(".legendColorBox").click(function () {
            if ($('.miniature').find('.legendColorBox').not('.no-img').length < 2 && !$(this).hasClass('no-img'))
                return;
            $(this).toggleClass('no-img');
            selected_datasets = [];
            $('.miniature').find('.legendColorBox').not('.no-img').each(function () {
                legend = $(this).next('.legendLabel').text();
                for (var idx in full_dataset) {
                    if (full_dataset[idx].label == legend) {
                        selected_datasets.push(full_dataset[idx]);
                    }
                }
            });
            plotAccordingToChoices();
        });
        plotAccordingToChoices();
    }

    function getSortedKeys(obj) {
        var keys = [];
        for (var key in obj) {
            keys.push(key);
        }
        return keys.sort();
    }

    function getSortedValues(obj) {
        var values = [];
        for (var value in obj) {
            values.push(value);
        }
        return values.sort();
    }

    function option_list_key(name, items, all) {
        to_return = '<select name="' + name + '" class="' + name + '">'
        if (all) {
            to_return += '<option value="All" >' + all + '</option>';
        }
        var sk = getSortedKeys(items);
        var l = sk.length;
        for (var i = 0; i < l; i++) {
            to_return += '<option value="' + sk[i] + '" >' + sk[i] + '</option>';
        }
        to_return += '</select>'
        return to_return;
    }

    function option_list_value(name, items, all) {
        to_return = '<select name="' + name + '" class="' + name + '">'
        if (all != null) {
            to_return += '<option value="All" >' + all + '</option>';
        }
        var sv = getSortedValues(items);
        var l = sv.length;
        for (var i = 0; i < l; i++) {
            to_return += '<option value="' + items[sv[i]] + '" >' + items[sv[i]] + '</option>';
        }
        to_return += '</select>'
        return to_return;
    }

    function add_data_output() {
        $('#data-outputs').append($.Mustache.render('dataRow', {
            vss: option_list_key('vss', virtual_sensor_to_fields, "All Virtual Sensors"),
            fields: option_list_key('fields', field_to_virtual_sensor, "All Fields")
        }));
    }

    function add_condition() {
        $('#conditions').append($.Mustache.render('conditionRow', {
            vss: option_list_key('vss', virtual_sensor_to_fields, "All Virtual Sensors"),
            fields: option_list_key('fields', field_to_virtual_sensor, "All Fields")
        }));

        $('.join:first').hide();
    }

    function prepare_form_for_submission(tag) {
        var counter = 0;
        tag.parents('form').find('.data-outputs-container .criterion').each(function () {
            $(this).find('.vss').each(function () {
                $(this).attr('name', 'vs[' + counter + ']');
            });
            $(this).find('.fields').each(function () {
                $(this).attr('name', 'field[' + counter + ']');
            });
            counter++;
        });
        counter = 0;
        tag.parents('form').find('.conditions-container .condition').each(function () {
            $(this).find('.vss').each(function () {
                $(this).attr('name', 'c_vs[' + counter + ']');
            });
            $(this).find('.fields').each(function () {
                $(this).attr('name', 'c_field[' + counter + ']');
            });
            $(this).find('.join').each(function () {
                $(this).attr('name', 'c_join[' + counter + ']');
            });
            $(this).find('.max').each(function () {
                $(this).attr('name', 'c_max[' + counter + ']');
                if (!$(this).val()) {
                    $(this).val('+Inf');
                }
            });
            $(this).find('.min').each(function () {
                $(this).attr('name', 'c_min[' + counter + ']');
                if (!$(this).val()) {
                    $(this).val('-Inf');
                }
            });
            counter++;
        });
    }


    function submit_form(tag, options, callback) {
        /* THIS IS A HELPER METHOD BUT NOT REALLY USED ANYWHRER ... */
        options = options || [];
        callback = callback || function () {};
        prepare_form_for_submission(tag);

        var to_send = tag.parents('form').serializeArray();
        //  alert(to_send);
        for (var format_key in options) {
            for (var idx in to_send) {
                //alert(to_send[idx] );
                if (to_send[idx].name == format_key) {
                    to_send[idx].value = options[format_key];
                }
            }
        }
    }

    function customRange(input) {
        return {
            minDate: (input.id == "datepicker_to" ? new Date($("#datepicker_from").val()) : null),
            maxDate: (input.id == "datepicker_from" ? new Date($("#datepicker_to").val()) : null)
        };
    }


var $doc = $(document);

$(function () {

    /** Load the REQUEST_LIST_OF_VIRTUAL_SENSORS xml and create the mapping */
    $.get("gsn?REQUEST=0", function (xml) {
        lovs = xml
        $('virtual-sensor', xml).each(function () {
            var vsname = $(this).attr('name');
            virtual_sensor_to_fields[vsname] = virtual_sensor_to_fields[vsname] || []
            $(this).find('field').each(function () {
                if ($(this).attr('category') != 'predicate') {
                    var field_name = $(this).attr('name');
                    virtual_sensor_to_fields[vsname].push(field_name);
                    field_to_virtual_sensor[field_name] = field_to_virtual_sensor[field_name] || []
                    field_to_virtual_sensor[field_name].push(vsname)
                }
            });
        });
        add_data_output();
        add_condition();
    });

    $('#add-data-output').click(function (e) {
        e.preventDefault();
        add_data_output();
    });

    $('#add-condition').click(function (e) {
        e.preventDefault();
        add_condition();
    });

    $('#nb').change(function () {
        var val = $(this).val();
        $('#nb_value').attr('disabled', (val == 'ALL'));
    });

    $('#agg_function').change(function () {
        $('#agg_period').attr('disabled', $(this).val() == '-1');
        $('#agg_unit').attr('disabled', $(this).val() == '-1');
    });

    $("#datepicker_from, #datepicker_to").fdatepicker({
        beforeShow: customRange,
        prevText: "",
        nextText: "",
        firstDay: 1,
        showOn: "both",
        buttonImage: "/style/calendar.png",
        buttonImageOnly: true,
        dateFormat: "dd/mm/yy 00:00:00"
    });

    $('.download_btn_csv').click(function () {
        $('#download_format').val('csv');
        prepare_form_for_submission($(this));
        return true;
    });

    $('.download_btn_xml').click(function () {
        $('#download_format').val('xml');
        prepare_form_for_submission($(this));
        return true;
    });

    $('.download_btn_pdf').click(function () {
        $('#download_format').val('pdf');
        var max_limit = 5000;
        var limit_enabled = $('#nb').val() != 'ALL';
        var limit = $('#nb_value').val();
        if (!limit_enabled || limit > max_limit) {
            $('#nb_value').val(max_limit);
            $('#nb_value').attr('disabled', false);
            $('#nb').val('SPECIFIED');
            alert("The report functionality is limited to " + max_limit + " rows per virtual sensor.");
        }
        prepare_form_for_submission($(this));
        return true;
    });

    $doc.on('click', '.remove-criterion, .remove-condition', function (e) {
        e.preventDefault();
        var criterion = $(this).closest('.criterion, .condition');
        if (criterion.parent().children().size() > 1) {
            criterion.remove();
        }
        $('.join:first').hide();
    });

    $doc.on('change', '.vss', function() {
        var vs = $(this).val();
        if (vs == 'All') {
            $(this).next("select").replaceWith(option_list_key('fields', field_to_virtual_sensor, "All Fields"));
        } else {
            $(this).next("select").replaceWith(option_list_value('fields', virtual_sensor_to_fields[vs], "All Fields"));
        }
    });

    $(".plot").click(function () {
        datasets = {};
        csv_to_json($(this), drawChart);
    });

    $(".grid").click(function () {
        function drawGrid(datasets) {
            $("#sensorSelect").empty();
            $("#sensorSelect").append('<option value="">Select a Virtual Sensor</option>');
            var displayed = [];
            for (var sensor in datasets) {
                sensor = sensor.split(' ')[0];
                if ($.inArray(sensor, displayed) < 0) {
                    displayed.push(sensor);
                    option = $("<option value=\"" + sensor + "\">" + sensor + "</option>");
                    $("#sensorSelect").append(option);
                }
            }

            $("#sensorSelect").change(function () {
                var choice = $("#sensorSelect").val();
                $('#gridContainer').empty();
                if (choice == '') return;
                // columns
                var fields = [];
                // column headers
                var headers = [];
                for (var sensor_field in datasets) {
                    if (sensor_field.substring(0, choice.length) == choice) {
                        fields.push(datasets[sensor_field]);
                        headers.push(sensor_field.substring(choice.length + 2, sensor_field.length - 1));
                    }
                }
                var data_length = fields[0]['data'].length;
                var grid = '<table id="grid" class="tablesorter" border="0" cellpadding="0" cellspacing="1"><thead><tr><th>timed</th>';
                for (var i = 0; i < headers.length; i++) {
                    grid += '<th>' + headers[i] + '</th>';
                }
                grid += '</tr></thead><tbody>';
                for (var i = 0; i < data_length; i++) {
                    // bug with the csv_to_json function: the last row is empty
                    if (fields[0]['data'][i][0]) {
                        grid += '<tr><td>' + fields[0]['data'][i][0] + '</td>';
                        for (var j = 0; j < fields.length; j++) {
                            grid += '<td>' + fields[j]['data'][i][1] + '</td>'
                        }
                        grid += '</tr>';
                    }
                }
                grid += '</tbody></table>';
                $('#gridContainer').html(grid);
                $('#grid').tablesorter({
                    widthFixed: true,
                    widgets: ['zebra']
                });
            });
        }
        csv_to_json($(this), drawGrid, 'iso');
    });

    // $('.accordion').accordion({
    //     header: ".ui-accordion-header",
    //     clearStyle: true
    // });

    $('.tabs ul').tabs();

    $('.ui-default-state').hover(
        function () {
            $(this).addClass('ui-hover-state');
        },
        function () {
            $(this).removeClass('ui-hover-state');
        });

    $.get('menu.jsp', {
        selected: "data"
    }, function (data) {
        $('#navigation').html(data);
    });
});
