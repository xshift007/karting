import { useEffect, useState, useMemo } from 'react'
import {
  Table, TableHead, TableBody, TableRow, TableCell,
  Paper, Typography
} from '@mui/material'
import sessionService from '../services/session.service'
import { format, addDays, startOfWeek } from 'date-fns'

const DOW = ['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY']

export default function WeeklyRack () {
  const [rack, setRack] = useState({})

  // lunes de la semana actual
  const monday = startOfWeek(new Date(), { weekStartsOn: 1 })
  const from   = format(monday, 'yyyy-MM-dd')
  const to     = format(addDays(monday, 6), 'yyyy-MM-dd')

  useEffect(() => {
    sessionService.weekly(from, to)
      .then(r => setRack(r.data))
      .catch(console.error)
  }, [from, to])

  /** Todos los rangos horarios existentes (sin duplicados) */
  const slots = useMemo(
    () => Array.from(
        new Set(
          Object.values(rack).flat()
            .map(s => `${s.startTime}-${s.endTime}`)
        )
      ).sort((a, b) => a.localeCompare(b)),
    [rack]
  )

  return (
    <Paper sx={{ p: 2 }}>
      <Typography variant='h5' gutterBottom>
        Disponibilidad (semana {from})
      </Typography>

      <Table size='small'>
        <TableHead>
          <TableRow>
            <TableCell>Hora</TableCell>
            {DOW.map(d => (
              <TableCell key={d}>{d.slice(0,3)}</TableCell>
            ))}
          </TableRow>
        </TableHead>

        <TableBody>
          {slots.map(range => {
            const [start] = range.split('-')
            return (
              <TableRow key={range}>
                <TableCell>{range}</TableCell>
                {DOW.map(d => {
                  const avail = rack[d]?.some(s => s.startTime === start)
                  return (
                    <TableCell key={d}>
                      {avail ? 'Disponible' : ''}
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
