import { useState } from 'react'
import { BarChart } from '@mui/x-charts/BarChart'
import { Stack, Button, Paper } from '@mui/material'
import reportService from '../services/report.service'
import { formatISO } from 'date-fns'

export default function ReportCharts(){
  const [dataRate,setRate]=useState([])
  const [dataGroup,setGroup]=useState([])

  const load=()=>{
    const from=formatISO(new Date('2025-01-01'),{representation:'date'})
    const to  =formatISO(new Date('2025-12-31'),{representation:'date'})
    reportService.byRate(from,to).then(r=>setRate(r.data))
    reportService.byGroup(from,to).then(r=>setGroup(r.data))
  }

  return (
    <Paper sx={{p:2}}>
      <Stack spacing={2}>
        <Button variant="contained" onClick={load}>Cargar datos 2025</Button>

        {dataRate.length>0 &&
          <BarChart width={500} height={300}
            xAxis={[{ scaleType:'band', data:dataRate.map(d=>d.rate) }]}
            series={[{ data:dataRate.map(d=>d.total) }]}
          />}

        {dataGroup.length>0 &&
          <BarChart width={500} height={300}
            xAxis={[{ scaleType:'band', data:dataGroup.map(d=>d.range) }]}
            series={[{ data:dataGroup.map(d=>d.total) }]}
          />}
      </Stack>
    </Paper>
  )
}
