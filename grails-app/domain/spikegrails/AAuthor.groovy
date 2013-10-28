package spikegrails

class AAuthor {
	static hasMany = [books: ABook]
	String name

    static constraints = {
    }

    static mapping = {
    	cache true
    }
}
