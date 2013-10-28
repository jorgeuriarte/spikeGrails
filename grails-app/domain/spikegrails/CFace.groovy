package spikegrails

class CFace {
	static hasOne = [nose: CNose]

    static constraints = {
    	nose unique: true
    }
}
