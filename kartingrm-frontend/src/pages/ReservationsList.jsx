// src/pages/ReservationForm.jsx
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

// … aquí tu schema de yup …

export default function ReservationForm(){
  const navigate = useNavigate()
  const location = useLocation()

  const [clients, setClients]   = useState([])
  const [sessions, setSessions] = useState([])

  const {
    control, setValue,
    handleSubmit, watch,
    formState:{ errors, isValid }
  } = useForm({
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

  // 1) Generar código al montar
  useEffect(()=>{
    setValue('reservationCode','R'+nanoid(6).toUpperCase())
  },[setValue])

  // 2) Parámetros URL para precarga
  useEffect(()=>{
    const p = new URLSearchParams(location.search)
    if (p.get('d')) setValue('sessionDate', p.get('d'))
    if (p.get('s')) setValue('startTime'  , p.get('s'))
    if (p.get('e')) setValue('endTime'    , p.get('e'))
  },[location.search, setValue])

  // 3) Cargar clientes
  useEffect(()=>{
    const fetchClients = async () => {
      try {
        const response = await clientService.getAll()
        setClients(response.data)
      } catch (e) {
        console.error("Error loading clients:", e)
      }
    }
    fetchClients()
  },[])

  // 4) Cargar **disponibilidad** de sesiones para la fecha
  useEffect(()=>{
    if (!sessionDate) return
    const fetchAvail = async () => {
      try {
        const { data } = await sessionService.weekly(sessionDate, sessionDate)
        // data es un objeto { MONDAY: [...], ... }
        const flat = Object.values(data).flat()
        setSessions(flat)
      } catch (e) {
        console.error("Error loading sessions:", e)
      }
    }
    fetchAvail()
  },[sessionDate])

  // 5) Slots disponibles (ya vienen filtrados del backend)
  const available = useMemo(
    () => sessions,
    [sessions]
  )

  // 6) Envío
  const onSubmit = async data => {
    try {
      const res = await reservationService.create(data)
      navigate(`/payments/${res.id}`, { replace:true })
    } catch(e) {
      alert(e.response?.data?.message || e.message)
    }
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

          {/* … resto del formulario, participantes y resumen … */}

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
