export const PRICES = {
  LAP_10 : 15000,
  LAP_15 : 20000,
  LAP_20 : 25000,
  WEEKEND: 20000,
  HOLIDAY: 25000
};

/**
 * Replica en el frontend el mismo cálculo de precio del backend.
 * @param {Object} p
 * @param {string} p.rateType
 * @param {number} p.participants
 * @param {number} [p.birthdayCount=0]
 * @param {number} [p.visitsThisMonth=0]
 */
export function computePrice ({
  rateType,
  participants,
  birthdayCount  = 0,
  visitsThisMonth = 0
}) {
  const base = PRICES[rateType] ?? 0;

  /* — descuento por tamaño de grupo — */
  const discGroup =
    participants <= 2 ? 0 :
    participants <= 5 ? 10 :
    participants <=10 ? 20 : 30;

  /* — descuento por cliente frecuente — */
  const discFreq =
    visitsThisMonth >= 7 ? 30 :
    visitsThisMonth >= 5 ? 20 :
    visitsThisMonth >= 2 ? 10 : 0;

  const subtotal   = base * participants;
  const afterGroup = subtotal * (1 - discGroup / 100);
  const afterFreq  = afterGroup * (1 - discFreq  / 100);

  /* — cumpleaños: 50 % a 1-2 personas — */
  const appliedBdays = Math.min(2, birthdayCount);
  const bdayAmount   = appliedBdays * base * 0.5;

  const final        = Math.round(afterFreq - bdayAmount);

  const discBirthPct = participants ? (bdayAmount * 100) / subtotal : 0;
  const totalDiscPct = (subtotal - final) * 100 / subtotal;

  return {
    base,
    discGroup,
    discFreq,
    discBirth : discBirthPct,
    totalDisc : totalDiscPct,
    final
  };
}
