package domain;
import java.time.LocalDateTime;
/**
 * Represents a new friendship
 * Ids are automatically numerically ordered
 */
public class Friendship extends Entity<Long> {
    private Long idUser1;
    private Long idUser2;
    private LocalDateTime data;

    public Friendship(Long idUser1,Long idUser2,LocalDateTime data){
        this.idUser1=Math.min(idUser1,idUser2);
        this.idUser2=Math.max(idUser1,idUser2);
        this.data=data;

    }
    public Long getIdUser1(){return this.idUser1;}
    public Long getIdUser2(){return this.idUser2;}
    public LocalDateTime getData(){return this.data;}

    @Override
    public String toString(){
        return "Friendship{"+"id="+getId()+",idUser1="+idUser1+",idUser2="+idUser2+"date of frienship"+data+'}';
    }
}
