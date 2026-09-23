const icons: Record<string, string> = {
  '三餐': '⌁',
  '零食': '◌',
  '服饰': '◇',
  '住房': '♜',
  '日用': '⊹',
  '护肤': '◒',
  '数码': '⌘',
  '交通': '➠',
  '人情': '♢',
  '医疗': '⚕',
  '通讯': '⌁',
  '羽毛球': '◈',
  '健身': '✧',
  '学习': '✒',
  '娱乐': '✦',
  '社交': '◍',
  '旅行': '✈',
  '工资': '◈',
  '红包': '✧',
  '副业': '⌘',
  '投资': '◉',
  '意外收入': '✦',
}

export function magicCategoryIcon(categoryName: string, fallback: string) {
  return icons[categoryName] ?? fallback
}
