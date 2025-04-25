import React, { useEffect, useState, useMemo } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { useForm, useFieldArray, Controller } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import * as yup from 'yup'
import dayjs from 'dayjs'
import { nanoid } from 'nanoid'
import {
  TextField, Button, Stack, Paper, Typography, MenuItem, IconButton
} from '@mui/material'
import { AddCircle, RemoveCircle } from '@mui/icons-material'
import reservationService from '../services/reservation.service'
import clientService      from '../services/client.service'
import sessionService     from '../services/session.service'
import { computePrice }   from '../helpers'

// Schema de validación
const schema = yup.object({
  reservationCode: yup.string().required(),
  clientId:        yup.number().required('Cliente obligatorio'),
  sessionDate:     yup.date().required('Fecha obligatoria'),
  startTime:       yup.string().required('Hora inicio obligatoria'),
  endTime:         yup.string()
    .required('Hora fin obligatoria')
    .test('is-after','Fin debe ser posterior', function(value){
      const { startTime } = this.parent
      if (!value || !startTime) return true
      return dayjs(value,'HH:mm').isAfter(dayjs(startTime,'HH:mm'))
    }),
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
  const location = useLocation()
  const [clients, setClients]   = useState([])
  const [sessions, setSessions] = useState([])
  const [counts, setCounts]     = useState({})

  const { control, setValue, handleSubmit, watch, formState:{ errors, isValid } } =
    useForm({
      resolver: yupResolver(schema),
      mode: 'onChange',
      defaultValues: {
        reservationCode: '',
        clientId:        '',
        sessionDate:     dayjs().format('YYYY-MM-DD'),
        startTime:       '',
        endTime:         '',
        participantsList:[{ fullName:'', email:'', birthday:false }],
        rateType:        'LAP_10'
      }
    })

  const { fields, append, remove } = useFieldArray({ control, name:'participantsList' })
  const sessionDate = watch('sessionDate')
  const startTime   = watch('startTime')
  const endTime     = watch('endTime')

  // 1) Generar código al montar
  useEffect(()=>{
    setValue('reservationCode','R'+nanoid(6).toUpperCase())
  },[setValue])

  // 2) Parámetros URL para precarga
  useEffect(()=>{
    const p = new URLSearchParams(location.search)
    if(p.get('d')) setValue('sessionDate', p.get('d'))
    if(p.get('s')) setValue('startTime'  , p.get('s'))
    if(p.get('e')) setValue('endTime'    , p.get('e'))
  },[location.search,setValue])

  // 3) Cargar clientes
  useEffect(() => {
        const controller = new AbortController()
        clientService.getAll({ signal: controller.signal })
          .then(r => setClients(r.data))
          .catch(err => {
            if (!controller.signal.aborted) console.error(err)
          })
        return () => controller.abort()
      }, [])

  // 4) Cargar sesiones y contar reservas
  useEffect(() => {
        if (!sessionDate) return
        const controller = new AbortController()
        sessionService.weekly(sessionDate, sessionDate, { signal: controller.signal })
          .then(r => {
            setSessions(r.data)
            const map = {}
            r.data.forEach(s => (map[s.id] = s.participantsCount || 0))
            setCounts(map)
          })
          .catch(err => {
            if (!controller.signal.aborted) console.error(err)
          })
        return () => controller.abort()
      }, [sessionDate])

  // 5) Filtrar slots que caben (cupo actual + nuevos ≤ capacidad)
  const available = useMemo(()=>{
    return sessions.filter(s=>
      (counts[s.id]||0) + fields.length <= s.capacity
    )
  },[sessions,counts,fields.length])

  // 6) Envío
  const onSubmit = data=>{
    reservationService.create(data)
      .then(res=> navigate(`/payments/${res.id}`, { replace:true }))
      .catch(e=> alert(e.response?.data?.message||e.message))
  }

  // 7) Resumen en tiempo real
  const summary = computePrice({
    rateType: watch('rateType'),
    participants: fields.length,
    birthdayCount: fields.filter(f=>f.birthday).length
  })

  return (
    <Paper sx={{p:3, maxWidth:600, mx:'auto'}}>
      <Typography variant="h5" gutterBottom>Crear Reserva</Typography>
      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <Stack spacing={2}>

          <Controller name="clientId" control={control}
            render={({ field })=>
              <TextField {...field} select label="Cliente"
                error={!!errors.clientId}
                helperText={errors.clientId?.message}>
                {clients.map(c=>
                  <MenuItem key={c.id} value={c.id}>{c.fullName}</MenuItem>
                )}
              </TextField>}
          />

          <Controller name="sessionDate" control={control}
            render={({ field })=>
              <TextField {...field} type="date" label="Fecha"
                InputLabelProps={{shrink:true}}
                error={!!errors.sessionDate}
                helperText={errors.sessionDate?.message}/>
            }
          />

          <Controller name="startTime" control={control}
            render={({ field })=>
              <TextField {...field} select label="Hora inicio"
                InputLabelProps={{shrink:true}}
                error={!!errors.startTime}
                helperText={errors.startTime?.message}>
                {available.map(s=>
                  <MenuItem key={s.id} value={s.startTime}>
                    {s.startTime}
                  </MenuItem>
                )}
              </TextField>}
          />

          <Controller name="endTime" control={control}
            render={({ field })=>
              <TextField {...field} select label="Hora fin"
                InputLabelProps={{shrink:true}}
                error={!!errors.endTime}
                helperText={errors.endTime?.message}>
                {available
                  .filter(s=>s.startTime===startTime)
                  .map(s=>
                    <MenuItem key={s.id} value={s.endTime}>
                      {s.endTime}
                    </MenuItem>
                  )}
              </TextField>}
          />

          <Controller name="rateType" control={control}
            render={({ field })=>
              <TextField {...field} select label="Tarifa">
                {['LAP_10','LAP_15','LAP_20','WEEKEND','HOLIDAY','BIRTHDAY']
                  .map(r=>
                    <MenuItem key={r} value={r}>
                      {r.replace('_',' ')}
                    </MenuItem>
                  )}
              </TextField>}
          />

          <Typography variant="h6">Participantes</Typography>
          {fields.map((item,idx)=>(
            <Stack key={item.id} direction="row" spacing={1} alignItems="center">
              <Controller name={`participantsList.${idx}.fullName`} control={control}
                render={({ field })=>
                  <TextField {...field} label="Nombre"
                    error={!!errors.participantsList?.[idx]?.fullName}
                    helperText={errors.participantsList?.[idx]?.fullName?.message}/>
                }
              />
              <Controller name={`participantsList.${idx}.email`} control={control}
                render={({ field })=>
                  <TextField {...field} label="Email"
                    error={!!errors.participantsList?.[idx]?.email}
                    helperText={errors.participantsList?.[idx]?.email?.message}/>
                }
              />
              <Controller name={`participantsList.${idx}.birthday`} control={control}
                render={({ field })=>
                  <TextField {...field} select label="Cumple">
                    <MenuItem value={false}>No</MenuItem>
                    <MenuItem value={true}>Sí</MenuItem>
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

          {/* Resumen debajo */}
          <Paper variant="outlined" sx={{p:2}}>
            <Typography variant="subtitle1">Resumen</Typography>
            <Typography>Participantes: {fields.length}</Typography>
            <Typography>Tarifa base: ${summary.base}</Typography>
            <Typography>Descuento: {summary.totalDisc}%</Typography>
            <Typography variant="h6">Total estimado: ${summary.final}</Typography>
          </Paper>

          <Stack direction="row" spacing={2} justifyContent="flex-end">
            <Button onClick={()=>navigate(-1)}>Cancelar</Button>
            <Button type="submit" variant="contained" disabled={!isValid}>
              Guardar
            </Button>
          </Stack>
        </Stack>
      </form>
    </Paper>
  )
}
