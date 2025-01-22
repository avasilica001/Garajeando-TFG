<?php
ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);

	require_once '../includes/OperacionesBBDD.php';
	
	$respuesta = array();
	
	if($_SERVER['REQUEST_METHOD']=='POST'){
		if (isset($_POST['CorreoElectronico']) and isset($_POST['Contrasena']) and isset($_POST['Nombre']) and isset($_POST['Apellidos']) and isset($_POST['Direccion'])){
			$bbdd = new DBOperations();

			if($bbdd->buscarCorreoElectronico($_POST['CorreoElectronico']) == 1){
				$respuesta['error'] = true;
				$respuesta['mensaje'] = "El correo electrónico introducido ya está en uso.";
			}else{
				if ($bbdd->crearUsuario($_POST['CorreoElectronico'],$_POST['Contrasena'],$_POST['Nombre'],$_POST['Apellidos'],$_POST['Direccion'],$_POST['FotoPerfil'],$_POST['NombreFoto'])){
					$respuesta['error'] = false;
					$respuesta['mensaje'] = "El usuario se ha añadido correctamente.";
				}else{
					$respuesta['error'] = true;
					$respuesta['mensaje'] = "El usuario NO se ha añadido correctamente.";
				}
		}
		}else{
			$respuesta['error'] = true;
			$respuesta['mensaje'] = "Faltan campos obligatorios.";
		}
	}else{
		$respuesta['error'] = true;
		$respuesta['mensaje'] = "Petición no válida.";
	}
	
echo json_encode($respuesta);
?>
