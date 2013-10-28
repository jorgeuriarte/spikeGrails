package spikegrails



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(BFace)
@Mock(BNose)
class BFaceTests {

    void testSaveFaceAndNoseOK() {
       new BFace(nose:new BNose()).save(failOnError:true, flush: true)

       assert BFace.count() == 1
       //http://grails.org/doc/latest/ref/Domain%20Classes/belongsTo.html
       //Many-to-one/one-to-one: saves and deletes cascade from the owner to the dependant (the class with the belongsTo).       
       assert BNose.count() == 1
    }

    void testSaveFaceAndNoseNOK() {
    	shouldFail {
       		new BNose(face:new BFace().save(failOnError:true)).save(failOnError:true, flush: true)
   		}
    }

    void testDeleteFaceCascadeNose() {
       	def face = new BFace(nose:new BNose()).save(failOnError:true, flush: true)

    	assert BFace.count() == 1
    	assert BNose.count() == 1 //belongsTo == cascade save

    	face.delete(flush: true)

    	assert BFace.count() == 0
      //http://grails.org/doc/latest/ref/Domain%20Classes/belongsTo.html
      //Many-to-one/one-to-one: saves and deletes cascade from the owner to the dependant (the class with the belongsTo).
    	assert BNose.count() == 0
    }
}
