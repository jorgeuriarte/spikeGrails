package spikegrails

class CBook {
    static belongsTo = CAuthor
    static hasMany = [authors:CAuthor]
    String title

    static constraints = {
    }
}
