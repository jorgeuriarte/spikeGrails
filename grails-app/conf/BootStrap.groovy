class BootStrap {

    def init = { servletContext ->
        new spikegrails.AAuthor(name: "AuthorA-1").save()
        new spikegrails.BAuthor(name: "AuthorB-1").save() 	

		new spikegrails.BFace(nose:new spikegrails.BNose()).save(failOnError:true, flush: true)        
    }
    
    def destroy = {
    }
}
