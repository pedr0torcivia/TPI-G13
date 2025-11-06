@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder

public class Tarifa {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String nombre;

  private float valorLitroCombustible;

  private float costoBaseKm;

  private float costoGestionTramo;

  private LocalDate vigenteDesde;
  private LocalDate vigenteHasta; // puede ser null si sigue vigente
}
