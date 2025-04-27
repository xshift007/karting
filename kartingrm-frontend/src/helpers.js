export const PRICES = {
  LAP_10 : 15000,
  LAP_15 : 20000,
  LAP_20 : 25000,
  WEEKEND: 20000,
  HOLIDAY: 25000
};

export const DURATIONS = {
  LAP_10 : 30,
  LAP_15 : 35,
  LAP_20 : 40
};

/**
 * Replica exacta del cálculo del backend.
 */
export function computePrice ({
  rateType,
  participants,
  birthdayCount = 0,
  visitsThisMonth = 0
}) {
  const base = PRICES[rateType] ?? 0;
  const subtotal = base * participants;

  /* % grupo */
  const discGroup =
    participants <= 2 ? 0 :
    participants <= 5 ? 10 :
    participants <=10 ? 20 : 30;

  /* % frecuente */
  const discFreq =
    visitsThisMonth >= 7 ? 30 :
    visitsThisMonth >= 5 ? 20 :
    visitsThisMonth >= 2 ? 10 : 0;

  /* % cumpleaños según reglas nuevas */
  let discBirth = 0;
  if (birthdayCount === 1 && participants >= 3 && participants <= 5) {
    discBirth = 50 / participants;
  } else if (birthdayCount >= 2 && participants >= 6 && participants <= 15) {
    discBirth = 100 / participants;     // 2 personas al 50 %
  }

  /* aplicación lineal % */
  const final = Math.round(
    subtotal * (1 - (discGroup + discFreq + discBirth) / 100)
  );
  const totalDisc = ((subtotal - final) * 100) / subtotal;

  return {
    base,
    discGroup,
    discFreq,
    discBirth,
    totalDisc,
    final
  };
}
