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
 * Cálculo idéntico al backend (descuentos secuenciales).
 */
export function computePrice ({
  rateType,
  participants,
  birthdayCount = 0,
  visitsThisMonth = 0
}) {
  const base = PRICES[rateType] ?? 0;

  /* % grupo */
  const g =
    participants <= 2 ? 0 :
    participants <= 5 ? 10 :
    participants <=10 ? 20 : 30;

  /* % frecuente */
  const f =
    visitsThisMonth >= 7 ? 30 :
    visitsThisMonth >= 5 ? 20 :
    visitsThisMonth >= 2 ? 10 : 0;

  /* cumpleañeros con 50 % */
  const winners =
    (birthdayCount === 1 && participants >= 3 && participants <= 5) ? 1 :
    (birthdayCount >= 2  && participants >= 6 && participants <=15) ? Math.min(2,birthdayCount) :
    0;

  /* precios unitarios tras aplicar secuencialmente */
  const afterGroup   = base * (1 - g / 100);
  const ownerUnit = afterGroup * (1 - f / 100);
  const unitReg   = afterGroup;
  const unitBirth = afterGroup * 0.5;

  const final = Math.round(
    unitReg   * (participants - winners) +
    unitBirth * winners
  );

  const totalDisc = ((base * participants - final) * 100) /
                    (base * participants);

  return {
    base,
    discGroup : g,
    discFreq  : f,
    discBirth : winners ? 50 : 0,
    totalDisc,
    final
  };
}
