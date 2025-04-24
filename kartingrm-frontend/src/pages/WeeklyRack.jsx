import { useState, useEffect } from 'react'
import sessionService from '../services/session.service'
import { startOfWeek, addDays, formatISO } from 'date-fns'
import Table from '@mui/material/Table'
import TableHead from '@mui/material/TableHead'
import TableBody from '@mui/material/TableBody'
import TableRow from '@mui/material/TableRow'
import TableCell from '@mui/material/TableCell'
import Paper from '@mui/material/Paper'
import Typography from '@mui/material/Typography'

export default function WeeklyRack(){
  const monday = startOfWeek(new Date(), { weekStartsOn:1 })
  const [rack, setRack] = useState({})

  useEffect(()=>{
    sessionService.weekly(formatISO(monday, { representation:'date'}))
      .then(r => setRack(r.data))
  }, [])

  return (
    <Paper sx={{ p:2 }}>
      <Typography variant="h5" gutterBottom>Disponibilidad semanal</Typography>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Hora</TableCell>
            {Array.from({length:7}).map((_,i)=>
              <TableCell key={i}>{addDays(monday,i).toLocaleDateString('es-CL',{weekday:'short', day:'2-digit'})}</TableCell>)}
          </TableRow>
        </TableHead>
        <TableBody>
          {/** para simplificar mostramos cada sesión como bloque individual */}
          {Object.entries(rack).map(([dow, list])=>(
            list.map(s => (
              <TableRow key={s.id}>
                <TableCell>{s.startTime}-{s.endTime}</TableCell>
                <TableCell colSpan={7}>{/* could shade by dow, omitting for brev. */}</TableCell>
              </TableRow>
            ))
          ))}
        </TableBody>
      </Table>
    </Paper>
  )
}
