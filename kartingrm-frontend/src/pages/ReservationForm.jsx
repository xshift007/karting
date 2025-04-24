import { useState } from 'react'
import reservationService from '../services/reservation.service'
import clientService from '../services/client.service'
import { TextField, Button, Stack, Paper, Typography } from '@mui/material'
import dayjs from 'dayjs'

export default function ReservationForm(){
  const [form, setForm] = useState({
    reservationCode:'', clientId:'', sessionDate:'',
    startTime:'', endTime:'', participantsList:[],
    rateType:'LAP_10'
  })

  const handle = e => setForm({...form,[e.target.name]:e.target.value})

  const submit = () =>{
    // simple participante único para demo
    const payload = {...form, participantsList:[
      {fullName:'Participante 1', email:'demo@mail.com', birthday:false}
    ]}
    reservationService.create(payload).then(()=> alert('Reserva creada'))
  }

  return (
    <Paper sx={{p:3}}>
      <Typography variant="h6">Nueva reserva</Typography>
      <Stack spacing={2}>
        <TextField label="Código" name="reservationCode" onChange={handle}/>
        <TextField label="ID Cliente" name="clientId" onChange={handle}/>
        <TextField type="date" label="Fecha" name="sessionDate"
                   InputLabelProps={{shrink:true}} onChange={handle}/>
        <TextField type="time" label="Inicio" name="startTime"
                   InputLabelProps={{shrink:true}} onChange={handle}/>
        <TextField type="time" label="Fin" name="endTime"
                   InputLabelProps={{shrink:true}} onChange={handle}/>
        <Button variant="contained" onClick={submit}>Guardar</Button>
      </Stack>
    </Paper>
  )
}
