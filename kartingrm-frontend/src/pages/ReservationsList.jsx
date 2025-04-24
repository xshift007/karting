import { useEffect,useState } from 'react'
import reservationService from '../services/reservation.service'
import { Table,TableHead,TableBody,TableRow,TableCell,Paper,
         Button, Stack } from '@mui/material'

export default function ReservationsList(){
  const [list,setList]=useState([])

  const load=()=>reservationService.list().then(r=>setList(r.data))
  useEffect(load,[])

  const cancel=id=>reservationService.cancel(id).then(load)

  return (
    <Paper sx={{p:2}}>
      <Stack direction="row" justifyContent="space-between" sx={{mb:2}}>
        <h3>Reservas</h3>
        <Button variant="contained" href="/reservations/new">Nueva</Button>
      </Stack>
      <Table size="small">
        <TableHead>
          <TableRow>
            {['Código','Cliente','Fecha','Hora','Participantes','Estado',''].map(h=>
              <TableCell key={h}>{h}</TableCell>)}
          </TableRow>
        </TableHead>
        <TableBody>
          {list.map(r=>(
            <TableRow key={r.id}>
              <TableCell>{r.reservationCode}</TableCell>
              <TableCell>{r.client.fullName}</TableCell>
              <TableCell>{r.session.sessionDate}</TableCell>
              <TableCell>{r.session.startTime}</TableCell>
              <TableCell>{r.participants}</TableCell>
              <TableCell>{r.status}</TableCell>
              <TableCell>
                {r.status==='PENDING' &&
                  <Button size="small" color="error"
                    onClick={()=>cancel(r.id)}>Cancelar</Button>}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </Paper>
  )
}
