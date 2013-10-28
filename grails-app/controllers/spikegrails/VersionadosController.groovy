package spikegrails

class VersionadosController {

    def index() {
		def objeto = new Versionado(valor: 1).save(flush:true)	

    	render """<pre>
    	Pruebas de cómo afectan los cambios al indicador "dirty" y al campo de "version" de
    	las clases de dominio de GORM.

    	Hemos creado un elemento nuevo, 'objeto'.

    	def objeto = new Versionado(valor: 1).save(flush:true)

    	Vale: ${objeto}

    	${g.link(action: 'Ver objeto', id:objeto.id) { "Ver objeto ${objeto.id}" } }
    	${g.link(action: 'Cambiar un campo', id:objeto.id, params:[cambio:'cambioCampoNoPersistente']) { "Cambiar campo no persistente del ${objeto.id}" } }
    	${g.link(action: 'Cambiar un campo', id:objeto.id, params:[cambio:'cambioValorPersistente']) { "Cambiar campo persistente del objeto ${objeto.id}" } }
    	${g.link(action: 'Cambiar un campo', id:objeto.id, params:[cambio:'cambioVersion']) { "Cambiar el campo de version del objeto ${objeto.id}" } }

    	</pre>"""
    }

    def "Cambiar un campo"() {  // recibe en 'cambio' el closure al que llamar para modificar el objeto
    	def objeto = Versionado.get(params.id)
    	def versionAntes = objeto.version
    	"${params.cambio}"(objeto)
    	def eraDirty = objeto.isDirty()
    	objeto.save(flush:true)
    	def versionFinal = objeto.version
    	render """<pre>
    		Se ha intentado cambiar un campo del objeto ${objeto} (${params.cambio.toUpperCase()}), y se ha hecho un flush explícito del objeto.
    		Es en ese momento (en el flush), cuando se actualiza el número de version.

    		Version anterior: ${versionAntes}
    		Version final:    ${versionFinal}

    		Dirty antes del flush?: ${eraDirty}

    		${versionFinal > versionAntes ? "LA VERSION SE HA ACTUALIZADO" : "<B>LA VERSION NO SE ACTUALIZA</B>"}

    		Resumen:
    			- Cambiar un campo no persistente no modifica la version
    			- Cambiar un campo persistente actualizará la versión al guardar/flush
    			- Cambiar el propio cambio de version a un valor, actualizará la versió (e ignorará el valor)


    	${g.link(action: 'Ver objeto', id:objeto.id) { "Ver objeto ${objeto.id}" } }
    	${g.link(action: 'Cambiar un campo', id:objeto.id, params:[cambio:'cambioCampoNoPersistente']) { "Cambiar campo no persistente del ${objeto.id}" } }
    	${g.link(action: 'Cambiar un campo', id:objeto.id, params:[cambio:'cambioValorPersistente']) { "Cambiar campo persistente del objeto ${objeto.id}" } }
    	${g.link(action: 'Cambiar un campo', id:objeto.id, params:[cambio:'cambioVersion']) { "Cambiar el campo de version del objeto ${objeto.id}" } }


    	${g.link(action: 'index') { 'Volver a crear nuevo objeto' } }

    	</pre>"""
    }

    private def cambioValorPersistente = { c ->
    	c.@valor++
    }

    private def cambioCampoNoPersistente = { c ->
    	c.@lista << "otro valor"
    	c.@valor = c.valor
    }

    private def cambioVersion = { c ->
    	c.@version = 234
    }

    def "Ver objeto"() {
    	def objeto = Versionado.read(params.id)
    	render """<pre>
    		Valor del objeto con id: ${params.id}:

    		${objeto}

    	${g.link(action: 'Ver objeto', id:objeto.id) { "Ver objeto ${objeto.id}" } }
    	${g.link(action: 'Cambiar un campo', id:objeto.id, params:[cambio:'cambioCampoNoPersistente']) { "Cambiar campo no persistente del ${objeto.id}" } }
    	${g.link(action: 'Cambiar un campo', id:objeto.id, params:[cambio:'cambioValorPersistente']) { "Cambiar campo persistente del objeto ${objeto.id}" } }
    	${g.link(action: 'Cambiar un campo', id:objeto.id, params:[cambio:'cambioVersion']) { "Cambiar el campo de version del objeto ${objeto.id}" } }

    	${g.link(action: 'index') { 'Volver a crear nuevo objeto' } }


    	</pre>"""
    }
}
