export const PRICES = {
    LAP_10: 15000,
    LAP_15: 20000,
    LAP_20: 25000,
    WEEKEND: 20000,
    HOLIDAY: 25000,
    BIRTHDAY: 0
  }
  
  export function computePrice({ rateType, participants, birthdayCount }) {
    const base = PRICES[rateType] || 0
    const discGroup =
      participants <= 2 ? 0 :
      participants <= 5 ? 10 :
      participants <= 10 ? 20 : 30
    const discBirth = birthdayCount
      ? (50 / participants) * birthdayCount
      : 0
    const totalDisc = discGroup + discBirth
    const final = Math.round(base * (1 - totalDisc / 100))
    return { base, discGroup, discBirth, totalDisc, final }
  }
  