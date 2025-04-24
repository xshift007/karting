import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm, useFieldArray, Controller } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import * as yup from 'yup'
import dayjs from 'dayjs'
import {
  TextField, Button, Stack, Paper, Typography, MenuItem, IconButton
} from '@mui/material'
import { AddCircle, RemoveCircle } from '@mui/icons-material'
import reservationService from '../services/reservation.service'
import clientService      from '../services/client.service'
import sessionService     from '../services/session.service'

// Schema de validación
const schema = yup.object({
  reservationCode: yup.string().required('Código obligatorio'),
  clientId:        yup.number().required('Cliente obligatorio'),
  sessionDate:     yup.date().required('Fecha obligatoria'),
  startTime:       yup.string().required('Hora inicio obligatoria'),
  endTime:         yup.string()
    .required('Hora fin obligatoria')
    .test('is-after', 'Fin debe ser después', (val, ctx) =>
      dayjs(val, 'HH:mm').isAfter(dayjs(ctx.parent.startTime, 'HH:mm'))
    ),
  participantsList: yup.array().of(
    yup.object({
      fullName: yup.string().required('Nombre obligatorio'),
      email:    yup.string().email('Email inválido').required('Email obligatorio'),
      birthday: yup.boolean()
    })
  ).min(1,'Al menos 1 participante').max(15,'Máximo 15 participantes'),
  rateType:       yup.string().required()
})

export default function ReservationForm(){
  const navigate = useNavigate()
  const [clients, setClients]   = useState([])
  const [sessions, setSessions] = useState([])

  const { control, handleSubmit, watch, formState: { errors } } = useForm({
    resolver: yupResolver(schema),
    defaultValues: {
      reservationCode: '',
      clientId:        '',
      sessionDate:     dayjs().format('YYYY-MM-DD'),
      startTime:       '10:00',
      endTime:         '10:30',
      participantsList: [{ fullName:'', email:'', birthday:false }],
      rateType:        'LAP_10'
    }
  })

  const { fields, append, remove } = useFieldArray({ control, name:'participantsList' })
  const sessionDate = watch('sessionDate')

  // Cargar clientes
  useEffect(()=>{
    clientService.getAll()
      .then(r=> setClients(r.data))
      .catch(console.error)
  },[])

  // Cargar sesiones disponibles al cambiar fecha
  useEffect(()=>{
    if (!sessionDate) return
    sessionService.weekly(sessionDate, sessionDate)
      .then(r=> setSessions(r.data))
      .catch(console.error)
  },[sessionDate])

  const onSubmit = data => {
    reservationService.create(data)
      .then(()=> navigate('/reservations', { replace:true }))
      .catch(err => alert(err.response?.data?.message || err.message))
  }

  return (
    <Paper sx={{p:3, maxWidth:600, mx:'auto'}}>
      <Typography variant="h5" gutterBottom>Crear Reserva</Typography>
      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <Stack spacing={2}>
          <Controller name="reservationCode" control={control}
            render={({ field }) =>
              <TextField {...field} label="Código" error={!!errors.reservationCode}
                helperText={errors.reservationCode?.message} />}
          />
          <Controller name="clientId" control={control}
            render={({ field }) =>
              <TextField {...field} select label="Cliente"
                error={!!errors.clientId} helperText={errors.clientId?.message}>
                {clients.map(c=>
                  <MenuItem key={c.id} value={c.id}>
                    {c.fullName}
                  </MenuItem>
                )}
              </TextField>}
          />
          <Controller name="sessionDate" control={control}
            render={({ field }) =>
              <TextField {...field} type="date" label="Fecha"
                InputLabelProps={{shrink:true}}
                error={!!errors.sessionDate} helperText={errors.sessionDate?.message}/>}
          />
          <Controller name="startTime" control={control}
            render={({ field }) =>
              <TextField {...field} type="time" label="Hora inicio"
                InputLabelProps={{shrink:true}}
                error={!!errors.startTime} helperText={errors.startTime?.message}/>}
          />
          <Controller name="endTime" control={control}
            render={({ field }) =>
              <TextField {...field} type="time" label="Hora fin"
                InputLabelProps={{shrink:true}}
                error={!!errors.endTime} helperText={errors.endTime?.message}/>}
          />
          <Controller name="rateType" control={control}
            render={({ field }) =>
              <TextField {...field} select label="Tarifa">
                {['LAP_10','LAP_15','LAP_20','WEEKEND','HOLIDAY','BIRTHDAY']
                  .map(r=><MenuItem key={r} value={r}>{r.replace('_',' ')}</MenuItem>)}
              </TextField>}
          />

          <Typography variant="h6">Participantes</Typography>
          {fields.map((item, idx)=>(
            <Stack key={item.id} direction="row" spacing={1} alignItems="center">
              <Controller name={`participantsList.${idx}.fullName`} control={control}
                render={({ field })=>
                  <TextField {...field} label="Nombre"
                    error={!!errors.participantsList?.[idx]?.fullName}
                    helperText={errors.participantsList?.[idx]?.fullName?.message}/>}
              />
              <Controller name={`participantsList.${idx}.email`} control={control}
                render={({ field })=>
                  <TextField {...field} label="Email"
                    error={!!errors.participantsList?.[idx]?.email}
                    helperText={errors.participantsList?.[idx]?.email?.message}/>}
              />
              <Controller name={`participantsList.${idx}.birthday`} control={control}
                render={({ field })=>
                  <TextField {...field} select label="Cumple">
                    <MenuItem value={false}>No</MenuItem>
                    <MenuItem value={true} >Sí</MenuItem>
                  </TextField>}
              />
              <IconButton onClick={()=>remove(idx)} disabled={fields.length===1}>
                <RemoveCircle/>
              </IconButton>
            </Stack>
          ))}
          <Button startIcon={<AddCircle/>}
            onClick={()=>append({ fullName:'', email:'', birthday:false })}>
            Agregar participante
          </Button>

          <Stack direction="row" spacing={2} justifyContent="flex-end">
            <Button onClick={()=>navigate(-1)}>Cancelar</Button>
            <Button type="submit" variant="contained">Guardar</Button>
          </Stack>
        </Stack>
      </form>
    </Paper>
  )
}
