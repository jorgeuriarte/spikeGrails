package spikegrails



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(AFace)
@Mock(ANose)
class AFaceTests {

    void testSaveFaceAndNoseOK() {
       new AFace(nose:new ANose()).save(failOnError:true, flush: true)

       assert AFace.count() == 1
       assert ANose.count() == 0 //No belongsTo == no cascade save
    }

    void testSaveFaceAndNoseNOK() {
      shouldFail {
        new ANose(face:new AFace().save(failOnError:true)).save(failOnError:true, flush: true)
      }
    }

    void testDeleteFaceNotCascadeNose() {
    	def face = new AFace(nose:new ANose().save(failOnError:true)).save(failOnError:true, flush: true)

    	assert AFace.count() == 1
    	assert ANose.count() == 1

    	face.delete(flush: true)

    	assert AFace.count() == 0
    	assert ANose.count() == 1 //No belongsTo == no cascade detele
    }
}
