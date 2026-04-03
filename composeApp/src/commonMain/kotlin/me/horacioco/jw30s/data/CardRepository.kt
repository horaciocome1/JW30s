package me.horacioco.jw30s.data

import me.horacioco.jw30s.domain.CardResourceMapper

class CardRepository(private val cardDao: CardDao) {
    suspend fun initializeCards() {
        val totalCount = cardDao.getTotalCount()
        if (totalCount > 0) return

        val cards = CardResourceMapper.ALL_CARD_IDS.map { id ->
            val category = when {
                id.startsWith("generic") -> "generic"
                id.startsWith("teaching") -> "teaching"
                id.startsWith("miracle") -> "miracle"
                else -> "unknown"
            }
            Card(id = id, category = category, hasBeenShown = false)
        }
        cardDao.insertAll(cards)
    }

    suspend fun getNextCard(): Card? {
        val card = cardDao.getRandomUnshownCard()
        if (card != null) return card

        // All cards have been shown, reset and pick again
        cardDao.resetAllCards()
        return cardDao.getRandomUnshownCard()
    }

    suspend fun markCardShown(cardId: String) {
        cardDao.markAsShown(cardId)
    }
}