package com.audioeffect.voicechanger.item

class ItemFolder {
    enum class ItemType {
        PARENT_FOLDER, FOLDER, FILE
    }

    var type: ItemType
        private set
    var path: String
        private set
    var name: String? = null
        private set
    var childCount = 0
        private set
    var dateModify: String? = null
        private set
    var duration: String? = null
        private set

    constructor(path: String) {
        type = ItemType.PARENT_FOLDER
        this.path = path
    }

    constructor(path: String, name: String?, childCount: Int, date: String?) {
        type = ItemType.FOLDER
        this.path = path
        this.name = name
        this.childCount = childCount
        dateModify = date
    }

    constructor(path: String, name: String?, duration: String?) {
        type = ItemType.FILE
        this.path = path
        this.name = name
        this.duration = duration
    }
}