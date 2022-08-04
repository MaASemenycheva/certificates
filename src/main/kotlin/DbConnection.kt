import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

object DbConnection {
    private var EMF: EntityManagerFactory? = null
    private var THREAD_LOCAL: ThreadLocal<EntityManager?>? = null

    init {
        EMF = Persistence.createEntityManagerFactory("postgres_pu")
        THREAD_LOCAL = ThreadLocal()
    }

    val entityManager: EntityManager?
        get() {
            var em = THREAD_LOCAL!!.get()
            if (em == null) {
                em = EMF!!.createEntityManager()
                THREAD_LOCAL!!.set(em)
            }
            return em
        }

    fun closeEntityManager() {
        val em = THREAD_LOCAL!!.get()
        if (em != null) {
            em.close()
            THREAD_LOCAL!!.set(null)
        }
    }

    fun closeEntityManagerFactory() {
        EMF!!.close()
    }

    fun beginTransaction() {
        entityManager!!.transaction.begin()
    }

    fun rollback() {
        entityManager!!.transaction.rollback()
    }

    fun commit() {
        entityManager!!.transaction.commit()
    }
}