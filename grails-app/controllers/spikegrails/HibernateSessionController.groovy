package spikegrails

class HibernateSessionController {

    def index() {
        render """
        <pre>
            Demostraciones de funcionamiento de la Session de Hibernate asociada a la request
            y a los controllers.

            Se basa en:

            - AAuthor:

            ${g.link(controller:'hibernateSession', action: 'requestsession paso 0') { 'Uso de la sesión asociada a la request' } }
            ${g.link(controller:'hibernateSession', action: 'readonly paso 0') { 'Uso de "readOnly" en las busquedas' } }
        </pre>
        """
    }

    def "requestsession paso 0"() {
    	def author = new AAuthor(name:"SessionA-Author0")

    	render """
    	<pre>
    		Acabamos de hacer un nuevo AAuthor, pero no hemos llamado a 'save'.
    		El objeto no se ha guardado.

    		${g.link(controller:'hibernateSession', action: 'requestsession paso 1') { 'Crear llamando a "save" al crear y al modificarlo.' } }
    	</pre>
    	"""
    }

    def "requestsession paso 1"() {
    	def author = new AAuthor(name:"SessionA-Author1")
    	println "Antes del save()"

    	author.save()

    	println "Despues del save() 1"

    	println "Antes de modificarlo"
    	author.name = "Session-Author1-changed"
    	println "Ya ha sido modificado."
    	println "NO llamamos a SAVE otra vez!! (pero se verá después un update derivado del fin de la request)"

    	render """
    	<pre>
    		Acabamos de hacer otro AAuthor, y hemos llamado a save()
    		El objeto se ha guardado (se ve la insert SQL) en el momento de invocar a save,
    		antes de terminar la request.

    		Después lo hemos modificado y NO hemos invocado a save(), pero al cerrarse la request, Hibernate
    		considera que el objeto está "sucio" (tiene cambios), y por lo tanto lo guarda automáticamente
    		(y se ve la 'update' en los logs de SQL)

    		${g.link(controller:'hibernateSession', action: 'requestsession paso 2') { 'Modificamos objeto descartado' } }
    	</pre>
    	"""
    }

    def "requestsession paso 2"() {
    	def author = new AAuthor(name:"SessionA-Author1")
    	println "Antes del save()"

    	author.save()
    	println "Despues del save() inicial"

    	author.discard()
    	println "Hemos desasociado el objeto de la session"

    	println "Antes de modificarlo"
    	(1..10).each {
	    	author.name = "Session-Author1-changed-${it}"
    	}
    	println "Ya ha sido modificado 100 veces. Pero no se hacen updates al cerrar session"

    	render """
    	<pre>
    		Acabamos de hacer otro AAuthor, y hemos llamado a save()
    		El objeto se ha guardado (se ve la insert SQL) en el momento de invocar a save,
    		antes de terminar la request.

    		Después lo hemos modificado y NO hemos invocado a save(), pero al cerrarse la request, Hibernate
    		considera que el objeto está "sucio" (tiene cambios), y por lo tanto lo guarda automáticamente
    		(y se ve la 'update' en los logs de SQL)

    		${g.link(controller:'hibernateSession', action: 'requestsession paso 3') { 'Hacemos flush explicito' } }
    	</pre>
    	"""    	
    }

    def "requestsession paso 3"() {
    	def author = new AAuthor(name:"SessionA-Author1")
    	println "Antes del save()"

    	author.save()
    	println "Despues del save() 1"

    	(1..5).each {
	    	println "Antes de modificarlo"
	    	author.name = "Session-Author1-changed-${it}"
	    	author.save(flush:true)
	    	println "Ya ha sido modificado."
	    }
    	println "Se llama a save en cada momento, porque incluye flush:true. Se ven las 5 updates"

    	render """
    	<pre>
    		Acabamos de hacer otro AAuthor, y hemos llamado a save()
    		El objeto se ha guardado (se ve la insert SQL) en el momento de invocar a save,
    		antes de terminar la request.

    		Después lo hemos modificado y NO hemos invocado a save(), pero al cerrarse la request, Hibernate
    		considera que el objeto está "sucio" (tiene cambios), y por lo tanto lo guarda automáticamente
    		(y se ve la 'update' en los logs de SQL)

    		${g.link(controller:'hibernateSession', action: 'index') { 'Volver' } }
    	</pre>
    	"""    	
    }

    def "readonly paso 0"() {
    	(1..10).each {
    		new AAuthor(name: "RO-${it}").save()
    	}
    	render """
    		Hemos creado una coleccion de objetos AAuthor, con nombre "RO-x"
    		En el siguiente paso, accederemos a ellos por nombre, y los modificaremos.

    		Se trata de comprobar cómo se puede pasar a los finders dinámicos el parámetro "readOnly",
    		que tiene el mismo efecto que llamar a ".discard()" sobre un objeto GORM obtenido en una request:
    		HIBERNATE NO SERÁ CONSCIENTE DE QUE DEBE ACTUALIZAR DICHO OBJETO AL FINALIZAR LA REQUEST.

    		${g.link(controller:'hibernateSession', action: 'readonly paso 1') { 'Siguiente' } }

    	"""
    }

    def "readonly paso 1"() {
    	def author1
    	def i=1
    	while (!author1) {
    		author1 = AAuthor.findByNameLike("RO-${i}")
    		println "Author: ${i} - ${author1}"
    		i++
    	}
    	author1.name = author1.name + "-changed"

    	render """
    		Se ha obtenido un objeto (${author1.name}), y se ha modificado. Se ve un UPDATE al terminar, generado automáticamente
    		por el cierre del hilo/session.

    		${g.link(controller:'hibernateSession', action: 'readonly paso 2') { 'Siguiente' } }

    	"""
    }

    def "readonly paso 2"() {
    	def author1
    	def i=1
    	while (!author1) {
    		author1 = AAuthor.findByNameLike("RO-${i}", [readOnly:true])
    		println "Author: ${i} - ${author1}"
    		i++
    	}

    	author1.name = author1.name + "-changed"

    	render """
    		Se ha obtenido un objeto (${author1.name}), pasando readOnly al finder. Al terminar, aunque el objeto se ha modificado,
    		NO SE VEN UPDATES!!

    		Si se llamara a 'save()' sobre esos objetos, sí se vería la update.

    		${g.link(controller:'hibernateSession', action: 'index') { 'Volver' } }

    	"""

    }
}
