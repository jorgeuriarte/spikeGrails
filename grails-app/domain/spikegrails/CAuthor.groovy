package spikegrails

class CAuthor {
    static hasMany = [books:CBook]
    String name

    static constraints = {
    }
}
