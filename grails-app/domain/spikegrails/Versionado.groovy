package spikegrails

class Versionado {

	Integer valor

	def lista = []

    static constraints = {
    }

    String toString() {
    	"Versionado: id=${id}, valor=${valor}, version=${version}, lista=${lista}"
    }
}
