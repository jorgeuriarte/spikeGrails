package spikegrails



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(CAuthor)
@Mock([CAuthor, CBook])
class CAuthorTests {

    void testSaveAuthorAndBookOK() {
		new CAuthor(name:"Stephen King")
		        .addToBooks(new CBook(title:"The Stand"))
		        .save(failOnError:true, flush: true)

    	assert CAuthor.count() == 1
        //http://grails.org/doc/latest/ref/Domain%20Classes/belongsTo.html
        //Many-to-many: only saves cascade from the "owner" to the "dependant"...
    	assert CBook.count() == 1
    }

    void testSaveAuthorAndBookNOK() {
    	shouldFail {
			new CBook(title:"The Stand")
			        .addToBooks(new CAuthor(title:"Stephen King"))
			        .save(failOnError:true, flush: true)
   		}
    }

    void testDeleteAuthorCascadeBook() {
		def author = new CAuthor(name:"Stephen King")
		        .addToBooks(new CBook(title:"The Stand"))
		        .save(failOnError:true, flush: true)

    	assert CAuthor.count() == 1
    	assert CBook.count() == 1 

    	author.delete(failOnError:true, flush: true)

    	assert CAuthor.count() == 0
        //http://grails.org/doc/latest/ref/Domain%20Classes/belongsTo.html
        //Many-to-many: ...but not deletes (cascade).
    	assert CBook.count() == 1 
    }
}
