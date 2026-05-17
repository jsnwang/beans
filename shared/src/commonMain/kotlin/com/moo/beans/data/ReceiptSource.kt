package com.moo.beans.data

/** How the line items for a receipt are obtained. */
enum class ReceiptSource {
    /** Capture a photo and OCR the items. */
    Scan,

    /** Type the items by hand. */
    Manual,
}
