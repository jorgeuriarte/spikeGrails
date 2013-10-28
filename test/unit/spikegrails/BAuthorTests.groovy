package spikegrails



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(BAuthor)
@Mock(BBook)
class BAuthorTests {
    void testSaveAuthorAndBookOK() {
		new BAuthor(name:"Stephen King")
		        .addToBooks(new BBook(title:"The Stand"))
		        .save(failOnError:true, flush: true)

    	assert BAuthor.count() == 1
    	assert BBook.count() == 1 // no belongsTo == cascade save
    }

    void testSaveAuthorAndBookNOK() {
    	shouldFail {
			new BBook(title:"The Stand")
			        .addToBooks(new BAuthor(title:"Stephen King"))
			        .save(failOnError:true, flush: true)
   		}
    }

    void testDeleteAuthorCascadeBook() {
		def author = new BAuthor(name:"Stephen King")
		        .addToBooks(new BBook(title:"The Stand"))
		        .save(failOnError:true, flush: true)

    	assert BAuthor.count() == 1
    	assert BBook.count() == 1 //no belongsTo == cascade save

    	author.delete(failOnError:true, flush: true)

    	assert BAuthor.count() == 0
    	assert BBook.count() == 1 //no belongsTo == No cascade delete
    }
}
