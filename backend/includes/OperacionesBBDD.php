<?php
ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);

        class DBOperations{
                private $conexion;

                function __construct(){
                        require_once dirname(__FILE__).'/ConexionBBDD.php';
						
			try{
        	                $bbdd = new DBConnect();
				$this->conexion = $bbdd->connect();
			}catch (Exception $e){
				echo "No se ha podido conectar a la base de dados.";
			}
                }

		public function generarStringRandom() {
			$longitud = 10;
    			$caracteresDisponibles = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    			$longitudCaracteres = strlen($caracteresDisponibles);
	    		$stringRandom = "";

   	 		for ($i = 0; $i < $longitud; $i++) {
       			 	$stringRandom .= $caracteresDisponibles[random_int(0, $longitudCaracteres - 1)];
    			}
		
   			 return $stringRandom;
		}


                public function crearUsuario($correo,$contrasena,$nombre,$apellidos,$direccion,$fotoperfil,$nombrefoto){

                        try {
                                $query = "INSERT INTO Usuarios (IdUsuario, CorreoElectronico,Contrasena,Nombre,Apellidos,Direccion,FotoPerfil) VALUES ('".$this->generarStringRandom()."','".$correo."','".$contrasena."','".$nombre."','".$apellidos."','".$direccion."','".$nombrefoto."');";
				$result = mysqli_query($this->conexion, $query);
					
				if (isset($fotoperfil) and isset($nombrefoto)){
					$path1 = "/var/www/html/imagenes/fotosperfil/$nombrefoto";
					$pathCompleto1 = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/$path1";
					file_put_contents($path1,base64_decode($fotoperfil));
				}
				
                                return true;
                        } catch (Exception $e) {
                                return false;
                        }
                }

		public function buscarCorreoElectronico($correo){
			try {
				$query = "SELECT CorreoElectronico FROM Usuarios WHERE CorreoElectronico = '".$correo."';";
				$result = mysqli_query($this->conexion, $query); 

				if (mysqli_num_rows($result) > 0) {
					return 1;
				}else{
					return 0;
				}
			} catch (Exception $e) {
				return false;
			}
		}

                public function verificarUsuario($correo,$contrasena){
                        try {
                                $query = "SELECT IdUsuario FROM Usuarios WHERE CorreoElectronico = '".$correo."' AND Contrasena = '".$contrasena."';";
                                $result = mysqli_query($this->conexion, $query); 

                                if (mysqli_num_rows($result) > 0) {
					$row = mysqli_fetch_row($result);
                                        return $row[0];
                                }else{
                                        return 0;
                                }
                        } catch (Exception $e) {
                                return false;
                        }
                }

		public function obtenerComunidades($idUsuario){
			try{
				$respuesta = array();

				$query = "SELECT C.IdComunidad,Nombre,CodInvitacion,Rol FROM UsuariosComunidades UC INNER JOIN Comunidades C ON UC.IdComunidad = C.IdComunidad WHERE IdUsuario = '".$idUsuario."' and Validado = 1;";
				$result = mysqli_query($this->conexion, $query);

				$i = 0;
				while ($row = mysqli_fetch_array($result)) {
					$respuesta[$i]['IdComunidad']=$row['IdComunidad'];
					$respuesta[$i]['Nombre']=$row['Nombre'];
					$respuesta[$i]['CodInvitacion']=$row['CodInvitacion'];
					$respuesta[$i]['Rol']=$row['Rol'];
					$i++;
				}
				return $respuesta;
			} catch (Exception $e){
				return false;
			}

		}

		public function buscarCodigoInvitacion($codInvitacion){
			try{
				$query = "SELECT IdComunidad from Comunidades WHERE CodInvitacion = '".$codInvitacion."';";
				$result = mysqli_query($this->conexion, $query);
				
				if (mysqli_num_rows($result) > 0) {
                                        $row = mysqli_fetch_array($result);
                                        return $row['IdComunidad'];
                                }else{
                                        return 0;
				}
			} catch (Exception $e){
				return false;
			}
		}

		public function anadirUsuarioAComunidad($idUsuario,$idComunidad,$validado,$rol){
			try{
                                $query = "INSERT INTO UsuariosComunidades (IdUsuario,IdComunidad,Validado,Rol) values('".$idUsuario."','".$idComunidad."','".$validado."','".$rol."');";
                                $result = mysqli_query($this->conexion, $query);

                                return true;
                        } catch (Exception $e){
                                return false;
                        }        
		}

		public function crearComunidad($nombreComunidad){
			try{
                                $query = "INSERT INTO Comunidades (IdComunidad,Nombre,CodInvitacion) values('".$this->generarStringRandom()."','".$nombreComunidad."','".$this->generarStringRandom()."');";
                                $result = mysqli_query($this->conexion, $query);

				$query2 = "SELECT IdComunidad FROM Comunidades WHERE Nombre = '".$nombreComunidad."';";
                                $result2 = mysqli_query($this->conexion, $query2); 

                                if (mysqli_num_rows($result2) > 0) {
                                        $row = mysqli_fetch_array($result2);
                                        return $row['IdComunidad'];
                                }else{
                                        return 0;
                                }
                        } catch (Exception $e){
                                return false;
                        }        
		}

		public function obtenerDatosComunidad($idComunidad){
                        try{
                                $query = "SELECT IdComunidad,Nombre,CodInvitacion FROM Comunidades WHERE IdComunidad = '".$idComunidad."';";
                                $result = mysqli_query($this->conexion, $query); 

                                if (mysqli_num_rows($result) > 0) {
                                        return mysqli_fetch_row($result);
                                }else{
                                        return 0;
                                }
                        } catch (Exception $e){
                                return false;
                        }        
                }

                public function obtenerCoches($idUsuario,$idComunidad){
                        try{
                                $respuesta = array();

                                $query = "SELECT C.IdCoche, Propietario, U.Nombre, U.Apellidos, Matricula, Marca, Modelo, Plazas, Puertas, Transmision, Combustible, AireAcondicionado, Bluetooth, GPS, Descripcion FROM Coches C INNER JOIN CochesComunidades CC ON C.IdCoche = CC.IdCoche INNER JOIN Usuarios U ON C.Propietario = U.IdUsuario WHERE Propietario = '".$idUsuario."' and IdComunidad = '".$idComunidad."'";
                                $result = mysqli_query($this->conexion, $query);

                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['IdCoche']=$row['IdCoche'];
					$respuesta[$i]['Propietario']=$row['Propietario'];
					$respuesta[$i]['NombrePropietario']=$row['Nombre'];
					$respuesta[$i]['ApellidosPropietario']=$row['Apellidos'];
					$respuesta[$i]['Matricula']=$row['Matricula'];
					$respuesta[$i]['Marca']=$row['Marca'];
					$respuesta[$i]['Modelo']=$row['Modelo'];
					$respuesta[$i]['Plazas']=$row['Plazas'];
					$respuesta[$i]['Puertas']=$row['Puertas'];
					$respuesta[$i]['Transmision']=$row['Transmision'];
					$respuesta[$i]['Combustible']=$row['Combustible'];
					$respuesta[$i]['AireAcondicionado']=$row['AireAcondicionado'];
					$respuesta[$i]['Bluetooth']=$row['Bluetooth'];
					$respuesta[$i]['GPS']=$row['GPS'];
					$respuesta[$i]['Descripcion']=$row['Descripcion'];

					$query2 = "SELECT FotoCoche FROM FotosCoches WHERE IdCoche='".$row['IdCoche']."' AND Principal=1;";
                                        $result2 = mysqli_query($this->conexion, $query2);
                                        $row2 = mysqli_fetch_array($result2);
					$respuesta[$i]['FotoCochePrincipal']=$row2['FotoCoche'];

                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }

                }

		public function obtenerCochesOtrasComunidades($idUsuario,$idComunidad){
			try{
                                $respuesta = array();

                                $query = "SELECT C.IdCoche, Propietario, U.Nombre, U.Apellidos, Matricula, Marca, Modelo, Plazas, Puertas, Transmision, Combustible, AireAcondicionado, Bluetooth, GPS, Descripcion FROM Coches C INNER JOIN CochesComunidades CC ON C.IdCoche = CC.IdCoche INNER JOIN Usuarios U ON U.IdUsuario = C.Propietario LEFT JOIN (SELECT C.IdCoche FROM Coches C INNER JOIN CochesComunidades CC ON C.IdCoche = CC.IdCoche INNER JOIN Usuarios U ON U.IdUsuario = C.Propietario WHERE C.Propietario = '".$idUsuario."' AND CC.IdComunidad = '".$idComunidad."') EX ON EX.IdCoche = C.IdCoche WHERE C.Propietario = '".$idUsuario."' AND CC.IdComunidad != '".$idComunidad."' AND EX.IdCoche IS NULL;";
                                $result = mysqli_query($this->conexion, $query);

                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['IdCoche']=$row['IdCoche'];
                                        $respuesta[$i]['Propietario']=$row['Propietario'];
					$respuesta[$i]['NombrePropietario']=$row['Nombre'];
					$respuesta[$i]['ApellidosPropietario']=$row['Apellidos'];
                                        $respuesta[$i]['Matricula']=$row['Matricula'];
                                        $respuesta[$i]['Marca']=$row['Marca'];
                                        $respuesta[$i]['Modelo']=$row['Modelo'];
                                        $respuesta[$i]['Plazas']=$row['Plazas'];
                                        $respuesta[$i]['Puertas']=$row['Puertas'];
                                        $respuesta[$i]['Transmision']=$row['Transmision'];
                                        $respuesta[$i]['Combustible']=$row['Combustible'];
                                        $respuesta[$i]['AireAcondicionado']=$row['AireAcondicionado'];
                                        $respuesta[$i]['Bluetooth']=$row['Bluetooth'];
                                        $respuesta[$i]['GPS']=$row['GPS'];
                                        $respuesta[$i]['Descripcion']=$row['Descripcion'];

					$query2 = "SELECT FotoCoche FROM FotosCoches WHERE IdCoche='".$row['IdCoche']."' AND Principal=1;";
                                        $result2 = mysqli_query($this->conexion, $query2);
                                        $row2 = mysqli_fetch_array($result2);
                                        $respuesta[$i]['FotoCochePrincipal']=$row2['FotoCoche'];

                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }
		}

		public function anadirCocheExistenteAComunidad($idCoche,$idComunidad){
                        try{
                                $query = "INSERT INTO CochesComunidades (IdCoche,IdComunidad) values('".$idCoche."','".$idComunidad."');";
                                $result = mysqli_query($this->conexion, $query);

                                return true;
                        } catch (Exception $e){
                                return false;
                        }        
                }

		public function obtenerFotosCoche($idCoche){
			try{
                                $respuesta = array();

                                $query = "SELECT FotoCoche FROM FotosCoches WHERE IdCoche = '".$idCoche."' ORDER BY Principal DESC;";
                                $result = mysqli_query($this->conexion, $query);

                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['FotoCoche']=$row['FotoCoche'];

                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }
		}

		public function obtenerCoche($idCoche){
                        try{
                                $respuesta = array();

                                $query = "SELECT C.IdCoche, Propietario, U.Nombre, U.Apellidos, Matricula, Marca, Modelo, Plazas, Puertas, Transmision, Combustible, AireAcondicionado, Bluetooth, GPS, Descripcion FROM Coches C INNER JOIN Usuarios U ON C.Propietario = U.IdUsuario WHERE IdCoche = '".$idCoche."';";
                                $result = mysqli_query($this->conexion, $query);

                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['IdCoche']=$row['IdCoche'];
                                        $respuesta[$i]['Propietario']=$row['Propietario'];
                                        $respuesta[$i]['NombrePropietario']=$row['Nombre'];
                                        $respuesta[$i]['ApellidosPropietario']=$row['Apellidos'];
                                        $respuesta[$i]['Matricula']=$row['Matricula'];
                                        $respuesta[$i]['Marca']=$row['Marca'];
                                        $respuesta[$i]['Modelo']=$row['Modelo'];
                                        $respuesta[$i]['Plazas']=$row['Plazas'];
                                        $respuesta[$i]['Puertas']=$row['Puertas'];
                                        $respuesta[$i]['Transmision']=$row['Transmision'];
                                        $respuesta[$i]['Combustible']=$row['Combustible'];
                                        $respuesta[$i]['AireAcondicionado']=$row['AireAcondicionado'];
                                        $respuesta[$i]['Bluetooth']=$row['Bluetooth'];
                                        $respuesta[$i]['GPS']=$row['GPS'];
                                        $respuesta[$i]['Descripcion']=$row['Descripcion'];

                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }

                }

		public function modificarDatosCoche($idCoche, $marca, $modelo, $plazas, $puertas, $transmision, $combustible, $aireAcondicionado, $bluetooth, $gps, $descripcion, $nombreFotoPrincipal, $fotoPrincipal){
                        try{
			
                                $query = "UPDATE Coches SET Marca='".$marca."', Modelo='".$modelo."', Plazas='".$plazas."', Puertas='".$puertas."', Transmision='".$transmision."', Combustible ='".$combustible."', AireAcondicionado ='".$aireAcondicionado."', Bluetooth ='".$bluetooth."', GPS ='".$gps."', Descripcion ='".$descripcion."' WHERE IdCoche = '".$idCoche."';";
                                $result = mysqli_query($this->conexion, $query);

				                                        
				if ($nombreFotoPrincipal !== "none") {
					$query2 = "SELECT FotoCoche FROM FotosCoches WHERE IdCoche='".$idCoche."' AND Principal=1;";
                                        $result2 = mysqli_query($this->conexion, $query2);

					$path1 = '/var/www/html/imagenes/fotoscoches/'.mysqli_fetch_row($result2)[0];
					
					if (file_exists($path1)) {
					    unlink($path1);
					}

					$query3 = "DELETE FROM FotosCoches WHERE IdCoche='".$idCoche."' AND Principal=1;";
                                        $result3 = mysqli_query($this->conexion, $query3);

					$query4 = "INSERT INTO FotosCoches (FotoCoche, IdCoche, Principal) values ('".$nombreFotoPrincipal."','".$idCoche."',1);";
                                        $result4 = mysqli_query($this->conexion, $query4);

                                        $path2 = "/var/www/html/imagenes/fotoscoches/$nombreFotoPrincipal";
                                        $pathCompleto2 = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/$path1";
                                        file_put_contents($path2,base64_decode($fotoPrincipal));
				}
                                return true;
                        } catch (Exception $e){
                                return false;
                        }        
                }

		public function crearOferta($idCoche, $idComunidad, $fechaHoraInicio, $fechaHoraFin){
                        try{

                               	$query = "SELECT idOferta FROM Ofertas WHERE ((FechaHoraInicio BETWEEN '".$fechaHoraInicio."' AND '".$fechaHoraFin."') OR (FechaHoraFin BETWEEN '".$fechaHoraInicio."' AND '".$fechaHoraFin."') OR (FechaHoraInicio <= '".$fechaHoraInicio."' AND FechaHoraFin >= '".$fechaHoraFin."')) AND IdCoche='".$idCoche."' AND IdComunidad='".$idComunidad."';";
                                $result = mysqli_query($this->conexion, $query);

	
                                if (mysqli_num_rows($result) > 0) {
					return false;
				}
				else{
                                        $query2 = "INSERT INTO  Ofertas (IdOferta, IdCoche, IdComunidad, FechaHoraInicio, FechaHoraFin, Reservada) VALUES ('".$this->generarStringRandom()."','".$idCoche."','".$idComunidad."','".$fechaHoraInicio."','".$fechaHoraFin."',0);";
                                        $result2 = mysqli_query($this->conexion, $query2);

                                	return true;
				}
                        } catch (Exception $e){
                                return false;
                        }
                }

		public function anadirCoche($idComunidad, $propietario, $matricula, $marca, $modelo, $plazas, $puertas, $transmision, $combustible, $aireAcondicionado, $bluetooth, $gps, $descripcion, $nombreFotoPrincipal, $fotoPrincipal){
                        try{
				$respuesta = array();

				$query0 = "SELECT Matricula FROM Coches WHERE Matricula = '".$matricula."';";
                                $result0 = mysqli_query($this->conexion, $query0); 

				if (mysqli_num_rows($result0) == 0) {
	                               	$idCoche = $this->generarStringRandom();

					$query = "INSERT INTO Coches (IdCoche, Propietario, Matricula, Marca, Modelo, Plazas, Puertas, Transmision, Combustible, AireAcondicionado, Bluetooth, GPS, Descripcion) values ('".$idCoche."','".$propietario."','".$matricula."','".$marca."','".$modelo."','".$plazas."','".$puertas."','".$transmision."','".$combustible."','".$aireAcondicionado."','".$bluetooth."','".$gps."','".$descripcion."');"; 
					$result = mysqli_query($this->conexion, $query);

					$query2 = "INSERT INTO CochesComunidades (IdCoche, IdComunidad) values('".$idCoche."','".$idComunidad."');";
                     	      		$result2 = mysqli_query($this->conexion, $query2);

                        	        if ($nombreFotoPrincipal !== "none") {
                           	             $query3 = "INSERT INTO FotosCoches (FotoCoche, IdCoche, Principal) values ('".$nombreFotoPrincipal."','".$idCoche."',1);";
                           	             $result3 = mysqli_query($this->conexion, $query3);

                                	     $path = "/var/www/html/imagenes/fotoscoches/$nombreFotoPrincipal";
                           		     $pathCompleto = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/$path";
                             		     file_put_contents($path,base64_decode($fotoPrincipal));
                                	}
					$respuesta[0]['error'] = false;
					$respuesta[0]['mensaje'] = 'OK';
				}
				else{
					$respuesta[0]['error'] = true;
					$respuesta[0]['mensaje'] = 'Ya existe un coche con esa matrícula';
				}
                                return $respuesta[0];
                        } catch (Exception $e){
				$respuesta[0]['error'] = true;
                                return $respuesta[0];
                        }
                }

		public function obtenerOfertasFuturas($idUsuario,$idComunidad){
                        try{
                                $respuesta = array();

                                $query ="SELECT IdOferta, O.IdCoche, IdComunidad, Matricula, FechaHoraInicio, FechaHoraFin, Reservada, FC.FotoCoche FROM Ofertas O INNER JOIN FotosCoches FC ON FC.IdCoche = O.IdCoche INNER JOIN Coches C ON C.IdCoche = O.IdCoche WHERE ((FechaHoraInicio < NOW() AND FechaHoraFin > NOW()) OR FechaHoraInicio > NOW()) AND IdComunidad = '".$idComunidad."' AND FC.Principal = 1 AND C.Propietario = '".$idUsuario."' ORDER BY FechaHoraInicio ASC;";
                                $result = mysqli_query($this->conexion, $query);

                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['IdOferta']=$row['IdOferta'];
                                        $respuesta[$i]['IdCoche']=$row['IdCoche'];
                                        $respuesta[$i]['IdComunidad']=$row['IdComunidad'];
					$respuesta[$i]['Matricula']=$row['Matricula'];
                                        $respuesta[$i]['FechaHoraInicio']=$row['FechaHoraInicio'];
                                        $respuesta[$i]['FechaHoraFin']=$row['FechaHoraFin'];
                                        $respuesta[$i]['FotoCoche']=$row['FotoCoche'];
					$respuesta[$i]['Reservada']=$row['Reservada'];

                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }

                }

		public function obtenerOfertasPasadas($idUsuario,$idComunidad){
                        try{
                                $respuesta = array();

                                $query ="SELECT IdOferta, O.IdCoche, IdComunidad, Matricula, FechaHoraInicio, FechaHoraFin, Reservada, FC.FotoCoche FROM Ofertas O INNER JOIN FotosCoches FC ON FC.IdCoche = O.IdCoche INNER JOIN Coches C ON C.IdCoche = O.IdCoche WHERE FechaHoraFin < NOW() AND IdComunidad = '".$idComunidad."' AND FC.Principal = 1 AND C.Propietario = '".$idUsuario."' ORDER BY FechaHoraInicio DESC;";
                                $result = mysqli_query($this->conexion, $query);

                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['IdOferta']=$row['IdOferta'];
                                        $respuesta[$i]['IdCoche']=$row['IdCoche'];
                                        $respuesta[$i]['IdComunidad']=$row['IdComunidad'];
                                        $respuesta[$i]['Matricula']=$row['Matricula'];
                                        $respuesta[$i]['FechaHoraInicio']=$row['FechaHoraInicio'];
                                        $respuesta[$i]['FechaHoraFin']=$row['FechaHoraFin'];
                                        $respuesta[$i]['FotoCoche']=$row['FotoCoche'];
					$respuesta[$i]['Reservada']=$row['Reservada'];

                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }

                }

		public function obtenerInfoOferta($idOferta){
                        try{
                                $respuesta = array();

                                $query ="SELECT IdOferta, IdCoche, IdComunidad, FechaHoraInicio, FechaHoraFin, Reservada FROM Ofertas WHERE IdOferta='".$idOferta."';";
                                $result = mysqli_query($this->conexion, $query);

                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['IdOferta']=$row['IdOferta'];
                                        $respuesta[$i]['IdCoche']=$row['IdCoche'];
                                        $respuesta[$i]['IdComunidad']=$row['IdComunidad'];
                                        $respuesta[$i]['FechaHoraInicio']=$row['FechaHoraInicio'];
                                        $respuesta[$i]['FechaHoraFin']=$row['FechaHoraFin'];
					$respuesta[$i]['Reservada']=$row['Reservada'];
                                
					$i++;
				}
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }

                }

		public function eliminarOferta($idOferta){
                        try{

                                $query = "DELETE FROM Ofertas WHERE IdOferta = '".$idOferta."';";
                                $result = mysqli_query($this->conexion, $query);

                                return true;
                        } catch (Exception $e){
                                return false;
                        }        
                }


		public function modificarOferta($idOferta, $fechaHoraInicio, $fechaHoraFin){
                        try{

                                $query = "UPDATE Ofertas SET FechaHoraInicio = '".$fechaHoraInicio."', FechaHoraFin = '".$fechaHoraFin."' WHERE idOferta = '".$idOferta."';";
                                $result = mysqli_query($this->conexion, $query);

                                return true;
                        } catch (Exception $e){
                                return false;
                        }        
                }

		public function obtenerOfertas($idUsuario,$idComunidad,$fechaHoraInicio,$fechaHoraFin){
                        try{
                                $respuesta = array();

                                $query = "SELECT IdOferta, O.IdCoche, IdComunidad, FechaHoraInicio, FechaHoraFin, Matricula, Reservada, FotoCoche FROM Ofertas O INNER JOIN Coches C ON C.IdCoche = O.IdCoche INNER JOIN FotosCoches FC ON C.IdCoche = FC.IdCoche WHERE ((FechaHoraInicio <= '".$fechaHoraInicio."' AND FechaHoraFin >= '".$fechaHoraFin."') OR (FechaHoraInicio = '".$fechaHoraInicio."' AND FechaHoraFin >= '".$fechaHoraFin."') OR (FechaHoraInicio <= '".$fechaHoraInicio."' AND FechaHoraFin = '".$fechaHoraFin."')) AND Principal = 1 AND O.IdComunidad = '".$idComunidad."' AND C.Propietario != '".$idUsuario."' AND Reservada = 0;";
                                $result = mysqli_query($this->conexion, $query);

                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
					$respuesta[$i]['IdOferta']=$row['IdOferta'];
                                        $respuesta[$i]['IdCoche']=$row['IdCoche'];
                                        $respuesta[$i]['IdComunidad']=$row['IdComunidad'];
                                        $respuesta[$i]['FechaHoraInicio']=$row['FechaHoraInicio'];
                                        $respuesta[$i]['FechaHoraFin']=$row['FechaHoraFin'];
                                        $respuesta[$i]['Matricula']=$row['Matricula'];
                                        $respuesta[$i]['FotoCoche']=$row['FotoCoche'];
					$respuesta[$i]['Reservada']=$row['Reservada'];

                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }
		}

		public function crearReserva($idUsuario,$idComunidad,$idCoche,$fechaHoraInicio,$fechaHoraFin,$idOferta){
                        try{
                               $query = "INSERT INTO Reservas (IdReserva, IdComunidad, IdUsuario, IdCoche, FechaHoraInicio, FechaHoraFin, Aprobada, Resena) VALUES ('".$this->generarStringRandom()."', '".$idComunidad."', '".$idUsuario."', '".$idCoche."', '".$fechaHoraInicio."', '".$fechaHoraFin."', 0, 0);";
                               $result = mysqli_query($this->conexion, $query);

                               $query2 = "UPDATE Ofertas SET Reservada = 1 WHERE IdOferta = '".$idOferta."';";
                               $result2 = mysqli_query($this->conexion, $query2); 

                                return true;
                        } catch (Exception $e){
                                return false;
                        }        
                }

		public function obtenerReservasPendientes($idUsuario,$idComunidad){
                        try{
                                $respuesta = array();

                                $query = "SELECT IdReserva, IdComunidad, R.IdUsuario, C.IdCoche, FechaHoraInicio, FechaHoraFin, FotoCoche, Matricula, Aprobada, Resena, C.Propietario, Nombre, Apellidos FROM Reservas R INNER JOIN Usuarios U ON R.IdUsuario = U.IdUsuario INNER JOIN Coches C ON R.IdCoche = C.IdCoche INNER JOIN FotosCoches F ON F.IdCoche = C.IdCoche WHERE IdComunidad = '".$idComunidad."' AND Propietario = '".$idUsuario."' and FechaHoraInicio > NOW() AND Principal = 1 AND Aprobada = 0 ORDER BY FechaHoraInicio ASC;";
                                $result = mysqli_query($this->conexion, $query);

                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['IdReserva']=$row['IdReserva'];
                                        $respuesta[$i]['IdCoche']=$row['IdCoche'];
                                        $respuesta[$i]['IdComunidad']=$row['IdComunidad'];
                                        $respuesta[$i]['FechaHoraInicio']=$row['FechaHoraInicio'];
                                        $respuesta[$i]['FechaHoraFin']=$row['FechaHoraFin'];
                                        $respuesta[$i]['Matricula']=$row['Matricula'];
                                        $respuesta[$i]['FotoCoche']=$row['FotoCoche'];
                                        $respuesta[$i]['Aprobada']=$row['Aprobada'];
					$respuesta[$i]['Resena']=$row['Resena'];
					$respuesta[$i]['IdUsuario']=$row['IdUsuario'];
					$respuesta[$i]['Propietario']=$row['Propietario'];
					$respuesta[$i]['Nombre']=$row['Nombre'];
					$respuesta[$i]['Apellidos']=$row['Apellidos'];

                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }
                }

		public function aceptarReserva($idReserva){
                        try{
                               $query = "UPDATE Reservas SET Aprobada = 1 WHERE IdReserva = '".$idReserva."';";
	                       $result = mysqli_query($this->conexion, $query); 

                                return true;
                        } catch (Exception $e){
                                return false;
                        }        
                }

		public function obtenerInfoReserva($idReserva){
                        try{
                                $respuesta = array();

                                $query ="SELECT IdReserva, IdComunidad, U.IdUsuario, U.Nombre, U.Apellidos, IdCoche, FechaHoraInicio, FechaHoraFin, Aprobada, Resena, PuntosUsuario, PuntosPropietario from Reservas R INNER JOIN Usuarios U ON R.IdUsuario = U.IdUsuario WHERE IdReserva = '".$idReserva."';";
                                $result = mysqli_query($this->conexion, $query);

                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['IdReserva']=$row['IdReserva'];
                                        $respuesta[$i]['IdCoche']=$row['IdCoche'];
                                        $respuesta[$i]['IdComunidad']=$row['IdComunidad'];
					$respuesta[$i]['IdUsuario']=$row['IdUsuario'];
					$respuesta[$i]['Nombre']=$row['Nombre'];
					$respuesta[$i]['Apellidos']=$row['Apellidos'];
                                        $respuesta[$i]['FechaHoraInicio']=$row['FechaHoraInicio'];
                                        $respuesta[$i]['FechaHoraFin']=$row['FechaHoraFin'];
                                        $respuesta[$i]['Aprobada']=$row['Aprobada'];
					$respuesta[$i]['Resena']=$row['Resena'];
					$respuesta[$i]['PuntosPropietario']=$row['PuntosPropietario'];
                                        $respuesta[$i]['PuntosUsuario']=$row['PuntosUsuario'];

                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }

                }


		public function denegarReserva($idReserva){
                        try{
                                $query = "SELECT IdOferta FROM Reservas R INNER JOIN Ofertas O ON (R.IdCoche = O.IdCoche AND R.IdComunidad = O.IdComunidad) WHERE R.FechaHoraInicio >= O.FechaHoraInicio AND R.FechaHoraFin <= O.FechaHoraFin AND O.Reservada = 1 AND R.IdReserva = '".$idReserva."';";
                                $result = mysqli_query($this->conexion, $query);

                                if (mysqli_num_rows($result) > 0) {
                                        $row = mysqli_fetch_array($result);
                                        
					$query2 = "UPDATE Ofertas SET Reservada = 0 WHERE idOferta = '".$row['IdOferta']."';";
                                	$result2 = mysqli_query($this->conexion, $query2);

					$query3 = "DELETE FROM Reservas WHERE IdReserva = '".$idReserva."';";
                                        $result3 = mysqli_query($this->conexion, $query3);

					return true;
                                }else{
                                        return false;
                                }
                        } catch (Exception $e){
                                return false;
                        }        
                }

		public function obtenerUsuario($idUsuario){
                        try{
                                $respuesta = array();

                                $query = "SELECT IdUsuario, CorreoElectronico, Nombre, Apellidos, Direccion, FotoPerfil, PuntosTotales FROM Usuarios WHERE IdUsuario = '".$idUsuario."';";
                                $result = mysqli_query($this->conexion, $query);

                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['IdUsuario']=$row['IdUsuario'];
                                        $respuesta[$i]['CorreoElectronico']=$row['CorreoElectronico'];
                                        $respuesta[$i]['Nombre']=$row['Nombre'];
                                        $respuesta[$i]['Apellidos']=$row['Apellidos'];
                                        $respuesta[$i]['Direccion']=$row['Direccion'];
                                        $respuesta[$i]['FotoPerfil']=$row['FotoPerfil'];
                                        $respuesta[$i]['PuntosTotales']=$row['PuntosTotales'];

                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }
                }

		public function modificarInfoUsuario($idUsuario,$correo,$nombre,$apellidos,$direccion,$fotoperfil,$nombrefoto){

                        try {

				if ($nombrefoto == "borrar") {
                                        $query = "SELECT FotoPerfil FROM Usuarios WHERE IdUsuario='".$idUsuario."';";
                                        $result = mysqli_query($this->conexion, $query);

					$row = mysqli_fetch_array($result);
					$rowfinal = $row['FotoPerfil'];
					
					if ($rowfinal !== "") {
                                        	$path1 = '/var/www/html/imagenes/fotosperfil/'.$rowfinal;

                                        	if (file_exists($path1)) {
	                                            	unlink($path1);
                                        	}

                                        	$nombrefoto = "";

					$query2 = "UPDATE Usuarios SET FotoPerfil = '".$nombrefoto."' WHERE IdUsuario='".$idUsuario."';";
                                        $result2 = mysqli_query($this->conexion, $query2);
					}
                                } elseif ($nombrefoto !== "none") {
					$query2 = "SELECT FotoPerfil FROM Usuarios WHERE IdUsuario='".$idUsuario."';";
                                        $result2 = mysqli_query($this->conexion, $query2);

					$row2 = mysqli_fetch_array($result2);
                                        $rowfinal2 = $row2['FotoPerfil'];

					if ($rowfinal2 !== "") {
                                        	$path2 = '/var/www/html/imagenes/fotosperfil/'.$rowfinal2;

                                        	if (file_exists($path2)) {
                                            		unlink($path2);
                                        	}
					}

					$path3 = "/var/www/html/imagenes/fotosperfil/$nombrefoto";
                                        $pathCompleto3 = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/$path3";
                                        file_put_contents($path3,base64_decode($fotoperfil));

					$query3 = "UPDATE Usuarios SET FotoPerfil = '".$nombrefoto."' WHERE IdUsuario='".$idUsuario."';";
                                        $result3 = mysqli_query($this->conexion, $query3);
				

				}

                                $query4 = "UPDATE Usuarios SET Nombre = '".$nombre."', Apellidos = '".$apellidos."', CorreoElectronico = '".$correo."', Direccion = '".$direccion."' WHERE IdUsuario= '".$idUsuario."';";
                                $result4 = mysqli_query($this->conexion, $query4);

                                return true;
                        } catch (Exception $e) {
                                return false;
                        }
                }

 		public function obtenerUsuariosAceptar($idComunidad){
                        try{
                                $respuesta = array();

                                $query = "SELECT UC.IdUsuario, CorreoElectronico, Nombre, Apellidos, Direccion, FotoPerfil, Rol FROM UsuariosComunidades UC INNER JOIN Usuarios U ON UC.IdUsuario = U.IdUsuario WHERE IdComunidad='".$idComunidad."' AND Validado = 0;";
                                $result = mysqli_query($this->conexion, $query);

                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['IdUsuario']=$row['IdUsuario'];
                                        $respuesta[$i]['CorreoElectronico']=$row['CorreoElectronico'];
                                        $respuesta[$i]['Nombre']=$row['Nombre'];
                                        $respuesta[$i]['Apellidos']=$row['Apellidos'];
                                        $respuesta[$i]['Direccion']=$row['Direccion'];
                                        $respuesta[$i]['FotoPerfil']=$row['FotoPerfil'];
					$respuesta[$i]['Rol']=$row['Rol'];

                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }
                }

		public function aceptarUsuarioAComunidad($idUsuario,$idComunidad){
                        try{
                               $query = "UPDATE UsuariosComunidades SET Validado = 1 WHERE IdUsuario = '".$idUsuario."' AND IdComunidad = '".$idComunidad."';";
                               $result = mysqli_query($this->conexion, $query); 

                                return true;
                        } catch (Exception $e){
                                return false;
                        }        
                }

		public function denegarUsuarioAComunidad($idUsuario,$idComunidad){
                        try{
                               $query = "DELETE FROM UsuariosComunidades WHERE IdUsuario = '".$idUsuario."' AND IdComunidad = '".$idComunidad."';";
                               $result = mysqli_query($this->conexion, $query); 

                                return true;
                        } catch (Exception $e){
                                return false;
                        }        
                }

		public function obtenerCodInvitacionComunidad($idComunidad){
                        try{
                               $query = "SELECT CodInvitacion FROM Comunidades WHERE IdComunidad = '".$idComunidad."';";
                               $result = mysqli_query($this->conexion, $query); 

                               if (mysqli_num_rows($result) > 0) {
                                        $row = mysqli_fetch_array($result);
                                        return $row['CodInvitacion'];
                                }else{
                                        return "";
                                }
                        } catch (Exception $e){
                                return "";
                        }        
                }

		public function actualizarCodigoInvitacion($idComunidad){
                        try{
                               $query = "UPDATE Comunidades SET CodInvitacion = '".$this->generarStringRandom()."' WHERE IdComunidad = '".$idComunidad."';";
                               $result = mysqli_query($this->conexion, $query); 

			       $query2 = "SELECT CodInvitacion FROM Comunidades WHERE IdComunidad = '".$idComunidad."';";
                               $result2 = mysqli_query($this->conexion, $query2); 

                               if (mysqli_num_rows($result2) > 0) {
                                        $row = mysqli_fetch_array($result2);
                                        return $row['CodInvitacion'];
                                }else{
                                        return "";
                                }
                        } catch (Exception $e){
                                return "";
                        }        
                }

		public function obtenerUsuariosComunidad($idComunidad){
                        try{
                                $respuesta = array();

                                $query = "SELECT UC.IdUsuario, CorreoElectronico, Nombre, Apellidos, Direccion, FotoPerfil, Rol  FROM UsuariosComunidades UC INNER JOIN Usuarios U ON UC.IdUsuario = U.IdUsuario WHERE IdComunidad='".$idComunidad."';";
                                $result = mysqli_query($this->conexion, $query);

                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['IdUsuario']=$row['IdUsuario'];
                                        $respuesta[$i]['CorreoElectronico']=$row['CorreoElectronico'];
                                        $respuesta[$i]['Nombre']=$row['Nombre'];
                                        $respuesta[$i]['Apellidos']=$row['Apellidos'];
                                        $respuesta[$i]['Direccion']=$row['Direccion'];
                                        $respuesta[$i]['FotoPerfil']=$row['FotoPerfil'];
					$respuesta[$i]['Rol']=$row['Rol'];

                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }
                }

		public function obtenerReservasFuturas($idUsuario,$idComunidad){
                        try{
                                $respuesta = array();

                                $query = "SELECT IdReserva, IdComunidad, R.IdUsuario, C.IdCoche, FechaHoraInicio, FechaHoraFin, FotoCoche, Matricula, Aprobada, Resena, C.Propietario, Nombre, Apellidos, PuntosPropietario, PuntosUsuario FROM Reservas R LEFT JOIN Usuarios U ON R.IdUsuario = U.IdUsuario INNER JOIN Coches C ON R.IdCoche = C.IdCoche INNER JOIN FotosCoches F ON F.IdCoche = C.IdCoche WHERE IdComunidad = '".$idComunidad."' AND C.Propietario = '".$idUsuario."' and FechaHoraFin > NOW() AND Principal = 1 AND Aprobada = 1 UNION SELECT IdReserva, IdComunidad, R.IdUsuario, C.IdCoche, FechaHoraInicio, FechaHoraFin, FotoCoche, Matricula, Aprobada, Resena, C.Propietario, Nombre, Apellidos, PuntosPropietario, PuntosUsuario FROM Reservas R LEFT JOIN Usuarios U ON R.IdUsuario = U.IdUsuario INNER JOIN Coches C ON R.IdCoche = C.IdCoche INNER JOIN FotosCoches F ON F.IdCoche = C.IdCoche WHERE IdComunidad = '".$idComunidad."' AND R.IdUsuario = '".$idUsuario."' and FechaHoraFin > NOW() AND Principal = 1 AND Aprobada = 1 ORDER BY FechaHoraInicio ASC;";
                                $result = mysqli_query($this->conexion, $query);
                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['IdReserva']=$row['IdReserva'];
                                        $respuesta[$i]['IdCoche']=$row['IdCoche'];
                                        $respuesta[$i]['IdComunidad']=$row['IdComunidad'];
                                        $respuesta[$i]['FechaHoraInicio']=$row['FechaHoraInicio'];
                                        $respuesta[$i]['FechaHoraFin']=$row['FechaHoraFin'];
                                        $respuesta[$i]['Matricula']=$row['Matricula'];
                                        $respuesta[$i]['FotoCoche']=$row['FotoCoche'];
                                        $respuesta[$i]['Aprobada']=$row['Aprobada'];
					$respuesta[$i]['Resena']=$row['Resena'];
                                        $respuesta[$i]['IdUsuario']=$row['IdUsuario'];
                                        $respuesta[$i]['Propietario']=$row['Propietario'];
                                        $respuesta[$i]['Nombre']=$row['Nombre'];
                                        $respuesta[$i]['Apellidos']=$row['Apellidos'];
					$respuesta[$i]['PuntosPropietario']=$row['PuntosPropietario'];
                                        $respuesta[$i]['PuntosUsuario']=$row['PuntosUsuario'];

                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }
                }

		public function obtenerReservasPasadas($idUsuario,$idComunidad){
                        try{
                                $respuesta = array();

                                $query = "SELECT IdReserva, IdComunidad, R.IdUsuario, C.IdCoche, FechaHoraInicio, FechaHoraFin, FotoCoche, Matricula, Aprobada, R.Resena, C.Propietario, Nombre, Apellidos, PuntosPropietario, PuntosUsuario FROM Reservas R LEFT JOIN Usuarios U ON R.IdUsuario = U.IdUsuario INNER JOIN Coches C ON R.IdCoche = C.IdCoche INNER JOIN FotosCoches F ON F.IdCoche = C.IdCoche WHERE IdComunidad = '".$idComunidad."' AND C.Propietario = '".$idUsuario."' and FechaHoraFin <= NOW() AND Principal = 1 AND Aprobada = 1 UNION SELECT IdReserva, IdComunidad, R.IdUsuario, C.IdCoche, FechaHoraInicio, FechaHoraFin, FotoCoche, Matricula, Aprobada, Resena, C.Propietario, Nombre, Apellidos, PuntosPropietario, PuntosUsuario FROM Reservas R LEFT JOIN Usuarios U ON R.IdUsuario = U.IdUsuario INNER JOIN Coches C ON R.IdCoche = C.IdCoche INNER JOIN FotosCoches F ON F.IdCoche = C.IdCoche WHERE IdComunidad = '".$idComunidad."' AND R.IdUsuario = '".$idUsuario."' and FechaHoraFin <= NOW() AND Principal = 1 AND Aprobada = 1 ORDER BY FechaHoraInicio ASC;";
                                $result = mysqli_query($this->conexion, $query);
                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['IdReserva']=$row['IdReserva'];
                                        $respuesta[$i]['IdCoche']=$row['IdCoche'];
                                        $respuesta[$i]['IdComunidad']=$row['IdComunidad'];
                                        $respuesta[$i]['FechaHoraInicio']=$row['FechaHoraInicio'];
                                        $respuesta[$i]['FechaHoraFin']=$row['FechaHoraFin'];
                                        $respuesta[$i]['Matricula']=$row['Matricula'];
                                        $respuesta[$i]['FotoCoche']=$row['FotoCoche'];
                                        $respuesta[$i]['Aprobada']=$row['Aprobada'];
					$respuesta[$i]['Resena']=$row['Resena'];
                                        $respuesta[$i]['IdUsuario']=$row['IdUsuario'];
                                        $respuesta[$i]['Propietario']=$row['Propietario'];
                                        $respuesta[$i]['Nombre']=$row['Nombre'];
                                        $respuesta[$i]['Apellidos']=$row['Apellidos'];
					$respuesta[$i]['PuntosPropietario']=$row['PuntosPropietario'];
                                        $respuesta[$i]['PuntosUsuario']=$row['PuntosUsuario'];

                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }
                }

		public function obtenerReservasPorResenar($idUsuario,$idComunidad){
                        try{
                                $respuesta = array();

                                $query = "SELECT IdReserva, IdComunidad, R.IdUsuario, C.IdCoche, FechaHoraInicio, FechaHoraFin, FotoCoche, Matricula, Aprobada, Resena, C.Propietario, Nombre, Apellidos, PuntosPropietario, PuntosUsuario FROM Reservas R LEFT JOIN Usuarios U ON R.IdUsuario = U.IdUsuario INNER JOIN Coches C ON R.IdCoche = C.IdCoche INNER JOIN FotosCoches F ON F.IdCoche = C.IdCoche WHERE IdComunidad = '".$idComunidad."' AND C.Propietario = '".$idUsuario."' and FechaHoraFin <= NOW() AND Principal = 1 AND Aprobada = 1 AND Resena = 0;";
                                $result = mysqli_query($this->conexion, $query);
                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['IdReserva']=$row['IdReserva'];
                                        $respuesta[$i]['IdCoche']=$row['IdCoche'];
                                        $respuesta[$i]['IdComunidad']=$row['IdComunidad'];
                                        $respuesta[$i]['FechaHoraInicio']=$row['FechaHoraInicio'];
                                        $respuesta[$i]['FechaHoraFin']=$row['FechaHoraFin'];
                                        $respuesta[$i]['Matricula']=$row['Matricula'];
                                        $respuesta[$i]['FotoCoche']=$row['FotoCoche'];
                                        $respuesta[$i]['Aprobada']=$row['Aprobada'];
                                        $respuesta[$i]['Resena']=$row['Resena'];
                                        $respuesta[$i]['IdUsuario']=$row['IdUsuario'];
                                        $respuesta[$i]['Propietario']=$row['Propietario'];
                                        $respuesta[$i]['Nombre']=$row['Nombre'];
                                        $respuesta[$i]['Apellidos']=$row['Apellidos'];
					$respuesta[$i]['PuntosPropietario']=$row['PuntosPropietario'];
					$respuesta[$i]['PuntosUsuario']=$row['PuntosUsuario'];

                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }
                }

		public function guardarPuntosResena($idUsuario,$matricula,$idReserva,$idUsuarioReserva,$fecha,$puntos,$minutos){
                        try{
                               $query = "UPDATE Reservas SET PuntosUsuario = '".$puntos."', PuntosPropietario = '".$minutos."', Resena = 1 WHERE IdReserva = '".$idReserva."';";
                               $result = mysqli_query($this->conexion, $query); 

                               $query2 = "INSERT INTO Puntos (IdUsuario, FechaHora, Puntos, Descripcion) values('".$idUsuarioReserva."','".$fecha."','".$puntos."','".$matricula."');";
                               $result2 = mysqli_query($this->conexion, $query2); 

			       $query3 = "INSERT INTO Puntos (IdUsuario, FechaHora, Puntos, Descripcion) values('".$idUsuario."','".$fecha."','".$minutos."','".$matricula."');";
                               $result3 = mysqli_query($this->conexion, $query3);

			       $query4 = "UPDATE Usuarios SET PuntosTotales = PuntosTotales + CAST('".$minutos."' AS SIGNED) WHERE '".$minutos."' REGEXP '^[+-]?[0-9]+$' AND IdUsuario = '".$idUsuario."';";
			       $result4 = mysqli_query($this->conexion, $query4); 

			       $query5 = "UPDATE Usuarios SET PuntosTotales = PuntosTotales + CAST('".$puntos."' AS SIGNED) WHERE '".$puntos."' REGEXP '^[+-]?[0-9]+$' AND IdUsuario = '".$idUsuarioReserva."';";
                               $result5 = mysqli_query($this->conexion, $query5); 

                               return true;
                        } catch (Exception $e){
                                return false;
                        }        
                }

		public function obtenerPuntos($idUsuario){
                        try{
                                $respuesta = array();

                                $query = "SELECT IdUsuario, FechaHora, Puntos, Descripcion FROM Puntos WHERE IdUsuario = '".$idUsuario."' ORDER BY FechaHora DESC;";
                                $result = mysqli_query($this->conexion, $query);
                                $i = 0;
                                while ($row = mysqli_fetch_array($result)){
                                        $respuesta[$i]['IdUsuario']=$row['IdUsuario'];
                                        $respuesta[$i]['FechaHora']=$row['FechaHora'];
                                        $respuesta[$i]['Puntos']=$row['Puntos'];
                                        $respuesta[$i]['Descripcion']=$row['Descripcion'];
                                        
                                        $i++;
                                }
                                return $respuesta;
                        } catch (Exception $e){
                                return false;
                        }
                }
	}
?>
