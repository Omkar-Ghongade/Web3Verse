import Highcharts from 'highcharts'
import React, { useEffect, useRef } from 'react'

interface LineChartProps {
  data: any[][]
}

const LineChart: React.FC<LineChartProps> = ({ data }) => {
  const chartRef = useRef(null)

  useEffect(() => {
    if (chartRef.current && data.length > 0) {
      Highcharts.chart(chartRef.current, {
        chart: {
          type: 'line',
        },
        title: {
          text: 'Portfolio Value',
        },
        xAxis: {
          type: 'datetime',
          title: {
            text: 'Date',
          },
        },
        yAxis: {
          title: {
            text: 'Price in INR',
          },
        },
        series: [
          {
            name: '',
            type: 'line',
            data: data,
          },
        ],
      })
    }
  }, [data])

  return (
    <div
      style={{
        padding: '1rem',
        borderRadius: '0.5rem',
        overflow: 'hidden',
      }}
    >
      <div ref={chartRef} />
    </div>
  )
}

export default LineChart
