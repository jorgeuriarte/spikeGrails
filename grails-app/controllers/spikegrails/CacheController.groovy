package spikegrails

class CacheController {

    def index() {
        render """
        <pre>
            Demostraciones de uso de las cachés.

            Se basa en:

            - AAuthor: mapping = { cache true }
            - BAuthor: mapping = { cache false }

            - Config.groovy

            ${g.link(controller:'cache', action: 'cachePrimerNivel') { 'Caché de primer nivel' } }
            ${g.link(controller:'cache', action: 'cacheSegundoNivel_0'){ 'Caché de segundo nivel'} }
        </pre>
        """
    }

    def cachePrimerNivel() {
        def author = new BAuthor(name:'SpareBAuthor').save(flush:true)
        println "\nEl autor es: ${BAuthor.get(author.id)}"
        (1..100).each {
            BAuthor.get(author.id)
        }

        BAuthor.findById(author.id)
        author.delete(flush:true)

        render """
        <pre>
            Demostración del llamado "caché de primer nivel".

            Creamos un nuevo BAuthor, que no tiene caché definido.
            Sin embargo, todo el resto de la petición, aunque hace múltiples veces 'get'
            no vuelve a emitir SQL después del INSERT. Esto se debe a que existe un caché de *primer nivel*
            que reside en la request HTTP. Durante esta petición, se guarda el objeto creado, y por lo tanto
            es directamente accesible, sin tirar de base de datos ni de caché EHCACHE.

            Sin embargo, cuando accedemos por BAuthor.findById, sí se emite una SELECT.
            Esto se debe a que el método findById no está incluído dentro de la caché de primer nivel (sí lo está el 'get(id)')

            Ejemplo de salida de la consola:

                Hibernate: insert into bauthor (id, version, name) values (null, ?, ?)

                El autor es: spikegrails.BAuthor : 10
                Hibernate: select this_.id as id10_0_, this_.version as version10_0_, this_.name as name10_0_ from bauthor this_ where this_.id=? limit ?
                Hibernate: delete from bauthor where id=? and version=?

            ${g.link(controller:'cache', action: 'index') { 'Volver' } }
        </pre>
        """
    }

    def cacheSegundoNivel_0() {
        println "\nCache de segundo nivel - 0 - INICIO"
        def b = BAuthor.findByName("AuthorB-1")
        BAuthor.get(b.id)
        render """<pre>
            Demostración de la "caché de segundo nivel" - 0

            Actuaremos sobre un AAuthor y un BAuthor creados en Bootstrap.groovy
            Hacemos una primera consulta sobre BAuthor (no tiene activado caché).
            Se hará la query si es el primer acceso. El .get(b.id) no hará nada, debido a la caché de PRIMER nivel.

            Consola:

                Hibernate: select this_.id as id11_0_, this_.version as version11_0_, this_.name as name11_0_ from bauthor this_ where this_.name=? limit ?

            Si recargo la página, vuelvo a ver esta traza.

            ${g.link(controller:'cache', action: 'cacheSegundoNivel_1', id: b.id) { 'Seguir'} }
        </pre>"""
        println "\nCache de segundo nivel - 0 - FIN"
    }

    def cacheSegundoNivel_1() {
        println "\nCache de segundo nivel - 1 - INICIO"        
        BAuthor.get(params.id)
        render """<pre>
            Demostración de la "caché de segundo nivel" - 1  (id: ${params.id})

            Hacemos un nuevo get. Vemos que la SQL se emite, no se ha cacheado.

            Consola:

                Hibernate: select bauthor0_.id as id11_0_, bauthor0_.version as version11_0_, bauthor0_.name as name11_0_ from bauthor bauthor0_ where bauthor0_.id=?

            Si recargo esta página, vuelvo a ver la traza cada vez.

            ${g.link(controller:'cache', action: 'cacheSegundoNivel_2') { 'Seguir'} }
        </pre>"""
        println "\nCache de segundo nivel - 1 - FIN"        
    }

    def cacheSegundoNivel_2() {
        println "\nCache de segundo nivel - 2 - INICIO"

        def a = AAuthor.findByName("AuthorA-1")
        AAuthor.get(a.id)
        render """<pre>
            Demostración de la "caché de segundo nivel" - 2

            Ahora hacemos la query sobre AAuthor (que SI tiene activado caché)
            Hacemos una primera consulta en esta página, y se ve la query.

            Se hará la query si es el primer acceso. El .get(b.id) no hará nada, debido a la caché de PRIMER nivel.

            Consola:

                Hibernate: select this_.id as id5_0_, this_.version as version5_0_, this_.name as name5_0_ from aauthor this_ where this_.name=? limit ?            

                Si recargamos veremos de nuevo la query, porque se está haciendo con un findByName no cacheado.

            ${g.link(controller:'cache', action: 'cacheSegundoNivel_3', id: a.id) { 'Seguir'} }
        </pre>"""
        println "\nCache de segundo nivel - 2 - FIN"

    }

    def cacheSegundoNivel_3() {
        println "\nCache de segundo nivel - 3 - INICIO"

        AAuthor.get(params.id)
        render """<pre>
            Demostración de la "caché de segundo nivel" - 3  (id: ${params.id})

            Hacemos un nuevo get. Vemos que la SQL NO SE EMITE, porque se ha cacheado

            Consola:

                [nada]

            Si recargo esta página, sigo sin ver la traza.

            ${g.link(controller:'cache', action: 'index') { 'Volver'} }
        </pre>"""
        println "\nCache de segundo nivel - 3 - FIN"

    }

}
