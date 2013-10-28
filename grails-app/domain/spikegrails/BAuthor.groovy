package spikegrails

class BAuthor {
	static hasMany = [books: BBook]
	String name

    static constraints = {
    }

    static mapping = {
    	cache false
    }
}
