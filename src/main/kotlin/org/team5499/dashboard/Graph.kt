package org.team5499.dashboard

import org.json.JSONObject

class Graph(vararg keys: String) {
    val values: MutableList<Pair<String, Double>> = mutableListOf()
    val labels: MutableList<Double> = mutableListOf()
    var chartData: JSONObject = JSONObject()
    var mKeys: Array<out String>
    init { mKeys = keys }

    fun addDataPoint(x: Double, y: Double, key: String) {
        labels += y
        values.add(Pair(key, x))
        // Unit test for values = keys
    }

    fun clear() {
        labels.clear()
        values.clear()
    }

    fun updateJSON(): JSONObject {
        // create a MutableList with an emtpy MutableList for each dataset
        var datasets: MutableList<MutableList<Any>> = mutableListOf()
        for (i in mKeys) {
            datasets.add(mutableListOf())
        }
        // sorts values into MutableList based on keys
        for (dataPoint in values) {
            datasets[mKeys.indexOf(dataPoint.toList()[0])].add(dataPoint.toList()[1])
        }
        chartData.put("lables", labels)
        chartData.put("datasets", datasets)
        return (chartData)
    }
}

// var chartData = {
//     labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
//     datasets: [{
//         type: 'line',
//         label: 'Dataset 1',
//         borderColor: window.chartColors.blue,
//         borderWidth: 2,
//         fill: false,
//         data: []
//     }
// }
