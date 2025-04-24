import React, { useEffect, useState, useMemo } from 'react'
import { Table, TableHead, TableBody, TableRow, TableCell, Paper, Typography } from '@mui/material'
import sessionService from '../services/session.service'
import { format, addDays, startOfWeek } from 'date-fns'
import { useNavigate } from 'react-router-dom'

const DOW = ['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY']

export default function WeeklyRack() {
  const [rack, setRack] = useState({})
  const navigate = useNavigate()
  const monday  = startOfWeek(new Date(), { weekStartsOn:1 })
  const from    = format(monday,'yyyy-MM-dd')
  const to      = format(addDays(monday,6),'yyyy-MM-dd')

  useEffect(()=>{
    sessionService.weekly(from,to)
      .then(r=> setRack(r.data))
      .catch(console.error)
  },[from,to])

  const slots = useMemo(() =>
    Array.from(new Set(
      Object.values(rack).flat().map(s=>`${s.startTime}-${s.endTime}`)
    )).sort((a,b)=>a.localeCompare(b))
  ,[rack])

  const onClickCell = (dayIdx, start, end) => {
    const date = format(addDays(monday,dayIdx),'yyyy-MM-dd')
    navigate(`/reservations/new?d=${date}&s=${start}&e=${end}`)
  }

  return (
    <Paper sx={{p:2}}>
      <Typography variant='h5' gutterBottom>
        Disponibilidad (semana {from})
      </Typography>
      <Table size='small'>
        <TableHead>
          <TableRow>
            <TableCell>Hora</TableCell>
            {DOW.map(d=><TableCell key={d}>{d.slice(0,3)}</TableCell>)}
          </TableRow>
        </TableHead>
        <TableBody>
          {slots.map(range=>{
            const [start,end] = range.split('-')
            return (
              <TableRow key={range}>
                <TableCell>{range}</TableCell>
                {DOW.map((d,idx)=>{
                  const avail = rack[d]?.some(s=>s.startTime===start)
                  return (
                    <TableCell
                      key={d}
                      onClick={()=> avail && onClickCell(idx,start,end)}
                      sx={{
                        cursor: avail?'pointer':'default',
                        color: avail?'green':'inherit'
                      }}
                    >
                      {avail?'Disponible':''}
                    </TableCell>
                  )
                })}
              </TableRow>
            )
          })}
        </TableBody>
      </Table>
    </Paper>
  )
}
