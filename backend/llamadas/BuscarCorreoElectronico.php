<?php
	require_once '../includes/OperacionesBBDD.php';
	
	$respuesta = array();
	
	if($_SERVER['REQUEST_METHOD']=='POST'){
		if (isset($_POST['CorreoElectronico'])){
			$bbdd = new DBOperations();
			
			$respuesta['error'] = false;
			$respuesta['mensaje'] = $bbdd->buscarCorreoElectronico($_POST['CorreoElectronico']);
			
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
