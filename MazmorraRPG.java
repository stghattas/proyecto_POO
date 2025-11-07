import java.util.*;
import java.util.concurrent.TimeUnit;

public class MazmorraRPG {
    // Sistema de clases del jugador
    enum Clase {
        CaballeroNegro("Caballero Negro", 120, 25, 15, "Espada de carton"),
        ELprogramador("El programador", 80, 35, 10, "Teclado magico"),
        AhstonHall("Ashton Hall", 100, 100, 280, "5 millones de dolares");
        
        final String nombre;
        final int vidaMaxima;
        final int ataque;
        final int defensa;
        final String armaInicial;
        
        Clase(String nombre, int vida, int ataque, int defensa, String arma) {
            this.nombre = nombre;
            this.vidaMaxima = vida;
            this.ataque = ataque;
            this.defensa = defensa;
            this.armaInicial = arma;
        }
    }
    
    // Entidades del juego
    static class Entidad {
        String nombre;
        int vida, vidaMaxima, ataque, defensa, nivel;
        
        Entidad(String nombre, int vida, int ataque, int defensa) {
            this.nombre = nombre;
            this.vida = this.vidaMaxima = vida;
            this.ataque = ataque;
            this.defensa = defensa;
            this.nivel = 1;
        }
        
        boolean estaVivo() { return vida > 0; }
        
        int atacar() {
            return (int)(ataque * (0.8 + Math.random() * 0.4));
        }
        
        void recibirDanio(int danio) {
            danio = Math.max(1, danio - defensa);
            vida = Math.max(0, vida - danio);
        }
    }
    
    static class Jugador extends Entidad {
        Clase clase;
        int experiencia, experienciaNecesaria;
        ArrayList<String> inventario;
        String armaEquipada;
        int oro;
        
        Jugador(Clase clase) {
            super("Héroe", clase.vidaMaxima, clase.ataque, clase.defensa);
            this.clase = clase;
            this.experiencia = 0;
            this.experienciaNecesaria = 100;
            this.inventario = new ArrayList<>();
            this.armaEquipada = clase.armaInicial;
            this.oro = 50;
            this.inventario.add(armaEquipada);
        }
        
        @Override
        int atacar() {
            int danioBase = super.atacar();
            // Bonus por arma
            if (armaEquipada.contains("Legendaria")) danioBase += 15;
            else if (armaEquipada.contains("Épica")) danioBase += 10;
            else if (armaEquipada.contains("Rara")) danioBase += 5;
            return danioBase;
        }
        
        void ganarExperiencia(int exp) {
            experiencia += exp;
            System.out.println("¡Ganaste " + exp + " puntos de experiencia!");
            
            while (experiencia >= experienciaNecesaria) {
                subirNivel();
            }
        }
        
        void subirNivel() {
            nivel++;
            experiencia -= experienciaNecesaria;
            experienciaNecesaria = (int)(experienciaNecesaria * 1.5);
            
            vidaMaxima += 20;
            vida = vidaMaxima;
            ataque += 5;
            defensa += 2;
            
            System.out.println("\n¡ SUBISTE AL NIVEL " + nivel + "!");
            System.out.println("Vida: " + vidaMaxima + " | Ataque: " + ataque + " | Defensa: " + defensa);
        }
        
        void usarPocion() {
            if (inventario.contains("Poción de Vida")) {
                int curacion = vidaMaxima / 2;
                vida = Math.min(vidaMaxima, vida + curacion);
                inventario.remove("Poción de Vida");
                System.out.println("¡Usaste una poción! Vida recuperada: +" + curacion);
            } else {
                System.out.println("No tienes pociones...");
            }
        }
    }
    
    // Sistema de mazmorra
    static class Sala {
        String descripcion;
        String evento; // "enemigo", "tesoro", "mercenario", "jefe"
        Entidad enemigo;
        String tesoro;
        
        Sala(String desc, String evento) {
            this.descripcion = desc;
            this.evento = evento;
        }
    }
    
    static class Piso {
        int numero;
        ArrayList<Sala> salas;
        String tema;
        
        Piso(int numero, String tema) {
            this.numero = numero;
            this.tema = tema;
            this.salas = new ArrayList<>();
        }
    }
    
    // Enemigos predefinidos
    static Entidad[] enemigosFaciles = {
        new Entidad("Goblin Gruñón", 40, 12, 3),
        new Entidad("Rata Gigante", 35, 10, 2),
        new Entidad("Esqueleto Tambaleante", 45, 14, 4)
    };
    
    static Entidad[] enemigosMedios = {
        new Entidad("Orco Bárbaro", 70, 20, 8),
        new Entidad("Araña Venenosa", 60, 18, 6),
        new Entidad("Mago Oscuro", 55, 25, 5)
    };
    
    static Entidad[] enemigosDificiles = {
        new Entidad("Minotauro", 100, 30, 12),
        new Entidad("Dragón Pequeño", 120, 35, 15),
        new Entidad("Caballero No-Muerto", 90, 28, 18)
    };
    
    static Entidad jefeFinal = new Entidad("REY DEMONIO MALAKAR", 200, 40, 20);
    
    public static void main(String[] args) throws InterruptedException {
        new MazmorraRPG().iniciarJuego();
    }
    
    void iniciarJuego() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        
        // Introducción
        mostrarTitulo();
        TimeUnit.SECONDS.sleep(2);
        
        System.out.println("\n NARRACIÓN: En el reino de Eldoria, una oscuridad antigua despierta...");
        System.out.println("Las mazmorras prohibidas han abierto sus puertas, y solo un héroe puede detenerlo.");
        TimeUnit.SECONDS.sleep(3);
        
        // Selección de clase
        Jugador jugador = seleccionarClase(scanner);
        
        // Crear mazmorra
        ArrayList<Piso> mazmorra = crearMazmorra();
        
        // ¡Aventura!
        System.out.println("\n Te adentras en las Profundidades Olvidadas...");
        TimeUnit.SECONDS.sleep(2);
        
        boolean juegoActivo = true;
        for (Piso piso : mazmorra) {
            if (!juegoActivo) break;
            
            System.out.println("\n===  PISO " + piso.numero + ": " + piso.tema + " ===");
            TimeUnit.SECONDS.sleep(1);
            
            for (int i = 0; i < piso.salas.size() && juegoActivo; i++) {
                Sala sala = piso.salas.get(i);
                juegoActivo = explorarSala(scanner, jugador, sala, i + 1);
                
                if (!jugador.estaVivo()) {
                    System.out.println("\n HAS CAÍDO EN BATALLA...");
                    juegoActivo = false;
                    break;
                }
            }
            
            if (juegoActivo && piso.numero < 3) {
                System.out.println("\n ¡Piso completado! Preparándote para el siguiente desafío...");
                jugador.vida = jugador.vidaMaxima; // Curar al cambiar de piso
                TimeUnit.SECONDS.sleep(2);
            }
        }
        
        // Final del juego
        if (jugador.estaVivo()) {
            finalEpico(jugador);
        } else {
            System.out.println("\nLa oscuridad consume Eldoria... Game Over.");
        }
        
        scanner.close();
    }
    
    void mostrarTitulo() {
        System.out.println("==================================================");
        System.out.println("      PROFUNDIDADES OLVIDADAS  ");
        System.out.println("       Un RPG de Mazmorra Épico");
        System.out.println("==================================================");
        System.out.println("       por Diego Rojas y Samer Ghattas  ");
        System.out.println("==================================================");
    }
    
    Jugador seleccionarClase(Scanner scanner) {
        System.out.println("\n ELIGE TU DESTINO:");
        for (int i = 0; i < Clase.values().length; i++) {
            Clase c = Clase.values()[i];
            System.out.println((i + 1) + ". " + c.nombre + " - Vida: " + c.vidaMaxima + 
                             " | Ataque: " + c.ataque + " | Defensa: " + c.defensa);
            System.out.println("   Arma: " + c.armaInicial);
        }
        
        while (true) {
            System.out.print("\nElige tu clase (1-3): ");
            try {
                int eleccion = Integer.parseInt(scanner.nextLine());
                if (eleccion >= 1 && eleccion <= 3) {
                    Clase claseElegida = Clase.values()[eleccion - 1];
                    System.out.println("\n¡Has elegido ser un " + claseElegida.nombre + "!");
                    return new Jugador(claseElegida);
                }
            } catch (NumberFormatException e) {}
            System.out.println("¡Elección inválida! Elige 1, 2 o 3.");
        }
    }
    
    ArrayList<Piso> crearMazmorra() {
        ArrayList<Piso> mazmorra = new ArrayList<>();
        
        // Piso 1
        Piso piso1 = new Piso(1, "Criptas Olvidadas");
        piso1.salas.add(crearSalaAleatoria(1));
        piso1.salas.add(crearSalaAleatoria(1));
        piso1.salas.add(new Sala("Sala del Guardián Antiguo", "jefe"));
        mazmorra.add(piso1);
        
        // Piso 2
        Piso piso2 = new Piso(2, "Cavernas Cristalinas");
        piso2.salas.add(crearSalaAleatoria(2));
        piso2.salas.add(crearSalaAleatoria(2));
        piso2.salas.add(new Sala("Santuario del Cristal", "tesoro"));
        mazmorra.add(piso2);
        
        // Piso 3
        Piso piso3 = new Piso(3, "Trono del Demonio");
        piso3.salas.add(crearSalaAleatoria(3));
        piso3.salas.add(crearSalaAleatoria(3));
        piso3.salas.add(new Sala("Salón del Trono Maldito", "jefe_final"));
        mazmorra.add(piso3);
        
        return mazmorra;
    }
    
    Sala crearSalaAleatoria(int dificultad) {
        String[] descripciones = {
            "Una sala circular con extraños grabados en las paredes...",
            "Un pasillo estrecho iluminado por antorchas azules...",
            "Una cámara amplia con columnas derruidas...",
            "Un puente sobre un abismo sin fondo...",
            "Un jardín subterráneo con plantas bioluminiscentes..."
        };
        
        String[] eventos = {"enemigo", "tesoro", "mercenario"};
        String evento = eventos[(int)(Math.random() * eventos.length)];
        
        Sala sala = new Sala(descripciones[(int)(Math.random() * descripciones.length)], evento);
        
        if (evento.equals("enemigo")) {
            Entidad[] grupoEnemigos;
            if (dificultad == 1) grupoEnemigos = enemigosFaciles;
            else if (dificultad == 2) grupoEnemigos = enemigosMedios;
            else grupoEnemigos = enemigosDificiles;
            
            sala.enemigo = new Entidad(
                grupoEnemigos[(int)(Math.random() * grupoEnemigos.length)].nombre,
                grupoEnemigos[(int)(Math.random() * grupoEnemigos.length)].vidaMaxima,
                grupoEnemigos[(int)(Math.random() * grupoEnemigos.length)].ataque,
                grupoEnemigos[(int)(Math.random() * grupoEnemigos.length)].defensa
            );
        }
        
        return sala;
    }
    
    boolean explorarSala(Scanner scanner, Jugador jugador, Sala sala, int numeroSala) throws InterruptedException {
        System.out.println("\n--- Sala " + numeroSala + " ---");
        System.out.println(sala.descripcion);
        TimeUnit.SECONDS.sleep(1);
        
        switch (sala.evento) {
            case "enemigo":
                return combate(scanner, jugador, sala.enemigo);
                
            case "tesoro":
                encontrarTesoro(jugador);
                break;
                
            case "mercenario":
                encontrarMercenario(scanner, jugador);
                break;
                
            case "jefe":
                Entidad jefePiso = new Entidad("Guardián Antiguo", 80, 25, 10);
                System.out.println("¡Un GUARDIÁN ANTIGUO bloquea el paso!");
                return combate(scanner, jugador, jefePiso);
                
            case "jefe_final":
                System.out.println("¡EL REY DEMONIO MALAKAR TE AGUARDA!");
                TimeUnit.SECONDS.sleep(2);
                System.out.println("Malakar: '¡Insignificante mortal! ¡Tu viaje termina aquí!'");
                return combateFinal(scanner, jugador);
        }
        
        return true;
    }
    
    boolean combate(Scanner scanner, Jugador jugador, Entidad enemigo) throws InterruptedException {
        System.out.println("\n⚔️ ¡COMBATE CONTRA " + enemigo.nombre.toUpperCase() + "!");
        
        while (jugador.estaVivo() && enemigo.estaVivo()) {
            System.out.println("\n--------------------------------");
            System.out.println(jugador.nombre + ": " + jugador.vida + "/" + jugador.vidaMaxima + " HP");
            System.out.println(enemigo.nombre + ": " + enemigo.vida + "/" + enemigo.vidaMaxima + " HP");
            System.out.println("--------------------------------");
            
            System.out.println("\n1. Atacar");
            System.out.println("2. Usar Poción");
            System.out.println("3. Intentar Huir");
            
            int accion = obtenerEleccion(scanner, 1, 3);
            
            switch (accion) {
                case 1:
                    int danioJugador = jugador.atacar();
                    System.out.println("¡Atacas con " + jugador.armaEquipada + "!");
                    System.out.println("Infliges " + danioJugador + " puntos de daño!");
                    enemigo.recibirDanio(danioJugador);
                    break;
                    
                case 2:
                    jugador.usarPocion();
                    break;
                    
                case 3:
                    if (Math.random() > 0.6) {
                        System.out.println("¡Logras escapar del combate!");
                        return true;
                    } else {
                        System.out.println("¡No puedes escapar!");
                    }
                    break;
            }
            
            // Turno del enemigo si sigue vivo
            if (enemigo.estaVivo()) {
                TimeUnit.MILLISECONDS.sleep(800);
                int danioEnemigo = enemigo.atacar();
                System.out.println("\n" + enemigo.nombre + " te ataca!");
                System.out.println("Recibes " + danioEnemigo + " puntos de daño!");
                jugador.recibirDanio(danioEnemigo);
            }
            
            TimeUnit.MILLISECONDS.sleep(800);
        }
        
        if (jugador.estaVivo()) {
            int experienciaGanada = enemigo.vidaMaxima / 3 + 20;
            int oroGanado = (int)(Math.random() * 30) + 10;
            
            System.out.println("\n🎊 ¡VICTORIA! Has derrotado a " + enemigo.nombre);
            jugador.ganarExperiencia(experienciaGanada);
            jugador.oro += oroGanado;
            System.out.println("¡Encuentras " + oroGanado + " monedas de oro!");
            
            // Posible drop de poción
            if (Math.random() > 0.7) {
                System.out.println("¡El enemigo soltó una Poción de Vida!");
                jugador.inventario.add("Poción de Vida");
            }
        }
        
        return jugador.estaVivo();
    }
    
    void encontrarTesoro(Jugador jugador) throws InterruptedException {
        System.out.println("\n💎 ¡HAS ENCONTRADO UN TESORO OCULTO!");
        TimeUnit.SECONDS.sleep(1);
        
        String[] tesoros = {
            "Espada Legendaria del Amanecer",
            "Armadura Épica de Diamante",
            "Anillo Raro de Poder Arcano",
            "Poción de Vida",
            "100 monedas de oro"
        };
        
        String tesoro = tesoros[(int)(Math.random() * tesoros.length)];
        
        if (tesoro.contains("monedas")) {
            jugador.oro += 100;
            System.out.println("¡Ganas 100 monedas de oro!");
        } else if (tesoro.equals("Poción de Vida")) {
            jugador.inventario.add("Poción de Vida");
            System.out.println("¡Añades una Poción de Vida a tu inventario!");
        } else {
            System.out.println("¡Encuentras: " + tesoro + "!");
            if (tesoro.contains("Espada")) {
                jugador.armaEquipada = tesoro;
                System.out.println("¡Equipas la nueva arma!");
            }
        }
    }
    
    void encontrarMercenario(Scanner scanner, Jugador jugador) throws InterruptedException {
        System.out.println("\n🛡️ Te encuentras con un mercenario ambulante...");
        System.out.println("Mercenario: '¡Objetos útiles para un aventurero! ¿Qué te interesa?'");
        TimeUnit.SECONDS.sleep(1);
        
        if (jugador.oro >= 40) {
            System.out.println("\n1. Poción de Vida - 40 oro (Recupera 50% de vida)");
            System.out.println("2. Mejora de Arma - 75 oro (+5 ataque permanente)");
            System.out.println("3. Nada, seguir adelante");
            
            int eleccion = obtenerEleccion(scanner, 1, 3);
            
            switch (eleccion) {
                case 1:
                    if (jugador.oro >= 40) {
                        jugador.oro -= 40;
                        jugador.inventario.add("Poción de Vida");
                        System.out.println("¡Comprada una Poción de Vida!");
                    } else {
                        System.out.println("¡No tienes suficiente oro!");
                    }
                    break;
                    
                case 2:
                    if (jugador.oro >= 75) {
                        jugador.oro -= 75;
                        jugador.ataque += 5;
                        System.out.println("¡Tu ataque aumenta permanentemente en 5 puntos!");
                    } else {
                        System.out.println("¡No tienes suficiente oro!");
                    }
                    break;
            }
        } else {
            System.out.println("Mercenario: 'No tienes suficiente oro... vuelve cuando tengas más.'");
        }
    }
    
    boolean combateFinal(Scanner scanner, Jugador jugador) throws InterruptedException {
        Entidad malakar = new Entidad("REY DEMONIO MALAKAR", 200, 40, 20);
        
        System.out.println("\n🔥 LA BATALLA FINAL HA COMENZADO!");
        
        int turno = 0;
        while (jugador.estaVivo() && malakar.estaVivo()) {
            turno++;
            System.out.println("\n=== TURNO " + turno + " ===");
            System.out.println(jugador.nombre + ": " + jugador.vida + "/" + jugador.vidaMaxima + " HP");
            System.out.println(malakar.nombre + ": " + malakar.vida + "/" + malakar.vidaMaxima + " HP");
            
            // Ataques especiales del jefe cada 3 turnos
            if (turno % 3 == 0) {
                System.out.println("\n🔥 Malakar lanza ALIENTO INFERNAL!");
                int danioEspecial = (int)(malakar.ataque * 1.5);
                System.out.println("¡Recibes " + danioEspecial + " puntos de daño!");
                jugador.recibirDanio(danioEspecial);
            } else {
                System.out.println("\n1. Atacar");
                System.out.println("2. Usar Poción");
                
                int accion = obtenerEleccion(scanner, 1, 2);
                
                if (accion == 1) {
                    int danioJugador = jugador.atacar();
                    System.out.println("¡Atacas con todas tus fuerzas!");
                    System.out.println("Infliges " + danioJugador + " puntos de daño!");
                    malakar.recibirDanio(danioJugador);
                } else {
                    jugador.usarPocion();
                }
            }
            
            // Turno de Malakar
            if (malakar.estaVivo()) {
                TimeUnit.MILLISECONDS.sleep(1000);
                int danioMalakar = malakar.atacar();
                System.out.println("\nMalakar: '¡SUFRIRÁS, INSIGNIFICANTE!'");
                System.out.println("Recibes " + danioMalakar + " puntos de daño!");
                jugador.recibirDanio(danioMalakar);
            }
            
            TimeUnit.MILLISECONDS.sleep(800);
        }
        
        return jugador.estaVivo();
    }
    
    void finalEpico(Jugador jugador) throws InterruptedException {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                    🏆 VICTORIA ÉPICA 🏆");
        System.out.println("=".repeat(50));
        TimeUnit.SECONDS.sleep(2);
        
        System.out.println("\nCon el Rey Demonio derrotado, la oscuridad se disipa...");
        TimeUnit.SECONDS.sleep(2);
        System.out.println("Eldoria está a salvo gracias a tu valentía, noble " + jugador.clase.nombre + "!");
        TimeUnit.SECONDS.sleep(2);
        
        System.out.println("\n📊 ESTADÍSTICAS FINALES:");
        System.out.println("   Nivel alcanzado: " + jugador.nivel);
        System.out.println("   Oro acumulado: " + jugador.oro + " monedas");
        System.out.println("   Arma final: " + jugador.armaEquipada);
        System.out.println("   Objetos en inventario: " + jugador.inventario.size());
        
        System.out.println("\n¡FELICIDADES, HÉROE! HAS COMPLETADO LAS PROFUNDIDADES OLVIDADAS");
    }
    
    int obtenerEleccion(Scanner scanner, int min, int max) {
        while (true) {
            System.out.print("\nElige una opción (" + min + "-" + max + "): ");
            try {
                int eleccion = Integer.parseInt(scanner.nextLine());
                if (eleccion >= min && eleccion <= max) {
                    return eleccion;
                }
            } catch (NumberFormatException e) {}
            System.out.println("¡Elección inválida! Intenta de nuevo.");
        }
    }
}