import { useEffect, useState } from 'react'
import { addDays, format } from 'date-fns'
import {
  Table, TableHead, TableBody, TableRow, TableCell,
  Paper, Typography
} from '@mui/material'
import sessionService from '../services/session.service'

export default function WeeklyRack(){
  const [rack, setRack] = useState({})
  const monday = new Date()
  // calcular lunes anterior
  while (monday.getDay() !== 1) monday.setDate(monday.getDate()-1)
  const from = format(monday,'yyyy-MM-dd')
  const to   = format(addDays(monday,6),'yyyy-MM-dd')

  useEffect(()=>{
    sessionService.weekly(from,to)
      .then(r=> setRack(r.data))
      .catch(console.error)
  },[from,to])

  return (
    <Paper sx={{p:2}}>
      <Typography variant="h5" gutterBottom>
        Disponibilidad (semana de {from})
      </Typography>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Hora</TableCell>
            {Array.from({length:7}).map((_,i)=>
              <TableCell key={i}>
                {format(addDays(monday,i),'EEE dd')}
              </TableCell>
            )}
          </TableRow>
        </TableHead>
        <TableBody>
          {/** Agrupamos por hora de inicio */}
          {Object.values(rack).flat()
            .sort((a,b)=> a.startTime.localeCompare(b.startTime))
            .map(s=>(
            <TableRow key={s.id}>
              <TableCell>{s.startTime}-{s.endTime}</TableCell>
              {Array.from({length:7}).map((_,i)=>{
                const day = format(addDays(monday,i),'yyyy-MM-dd')
                const has = rack[day]?.some(x=>x.id===s.id)
                return <TableCell key={i}>
                  {has? 'Disponible': ''}
                </TableCell>
              })}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </Paper>
  )
}
