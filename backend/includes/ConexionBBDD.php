<?php
	$respuesta = array();

	class DBConnect{
		private $conexion;

		function __construct(){
		}

		function connect(){
			include_once dirname(__FILE__).'/Constantes.php';
					
			try{
				$this->conexion = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
				return $this->conexion;
			}catch(Exception $e){
				$respuesta['error'] = true;
				$respuesta['mensaje'] = "No se ha podido establecer la conexión con la base de datos.";
				echo json_encode($respuesta);
				return null;
			}
		}
	}
?>
